package uk.co.nickthecoder.kogo

import javafx.application.Platform
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.paratask.util.process.BufferedSink
import uk.co.nickthecoder.paratask.util.process.Exec
import java.io.OutputStreamWriter
import java.io.Writer
import java.util.*

/**
 * An interface to GnuGo's Process. One instance can be shared my multiple clients, for example, one client may be a
 * GnuGoPlayer, and another is used to create hints or calculate the score at the end of the game.
 *
 * Uses the GTP protocal, running in a separate process. Therefore the results from a command are not returned immediatly,
 * instead the results are passed back via GnuGoClient.
 */
class GnuGo(val game: Game, level: Int) : GameListener {

    val rules = if (game.metaData.japaneseRules) "japanese" else "chinese"

    val exec = Exec("gnugo", "--mode", "gtp", "--level", level, "--boardsize", game.board.size, "--komi", game.metaData.komi, "--$rules-rules")

    var writer: Writer? = null

    var generatedPoint: Point? = null

    private val destinations = Collections.synchronizedList(ArrayList<GnuGoClient?>())

    fun start() {
        game.listeners.add(this)
        exec.outSink = object : BufferedSink() {
            override fun sink(line: String) {
                parseLine(line)
            }
        }
        exec.start()

        writer = OutputStreamWriter(exec.process!!.outputStream)

        println("Started GnuGo")
    }

    fun generateMove(color: StoneColor, client: GnuGoClient) {
        command("1 genmove ${color.toString().toLowerCase()}", client)
    }

    /**
     * Generate a move, but do not acturally play it. Used to generate Hints.
     */
    fun generateHint(color: StoneColor, client: GnuGoClient) {
        command("4 reg_genmove ${color.toString().toLowerCase()}", client)
    }

    @Synchronized
    private fun command(command: String, client: GnuGoClient?) {
        destinations.add(client)
        generatedPoint = null
        println("Sending command : '$command'")
        writer?.let {
            it.appendln(command)
            it.flush()
        }
    }

    private fun parseLine(line: String) {

        if (!line.startsWith("=")) {
            return
        }
        val listener = destinations.removeAt(0)

        println("Parsing reply: '$line'")

        if ((line.startsWith("=1 ") || line.startsWith("=4 ")) && line.length >= 5) {
            if (line.startsWith("=1 resign")) {
                listener?.generatedResign()
                return
            }
            if (line.startsWith("=1 PASS")) {
                listener?.generatedPass()
                return
            }
            val point = Point.fromString(line.substring(3))
            if (line.startsWith("=1")) {
                // Remember the point that was just generated, so that when stoneChanged is called, we don't
                // attempt to add the stone again.
                generatedPoint = point
            }
            listener?.generatedMove(point)
            return

        } else if (line.startsWith("=2 ")) {
            game.countedEndGame(line.substring(2).trim())

        } else if (line.startsWith("=3")) {

            val data = line.substring(2)
            val space = data.indexOf(" ")
            if (space > 0) {
                val pointNumber = Integer.parseInt(data.substring(0, space))
                val y = pointNumber / game.board.size
                val x = pointNumber - (y * game.board.size)
                val status = data.substring(space).trim()
                Platform.runLater {
                    markBoard(Point(x, y), status)
                }
            }
        }
    }

    fun markBoard(point: Point, status: String) {
        // Status is one of : "alive", "dead", "seki", "white_territory", "black_territory", or "dame"
        if (status == "white_territory") {
            game.addMark(TerritoryMark(point, StoneColor.WHITE))
        } else if (status == "black_territory") {
            game.addMark(TerritoryMark(point, StoneColor.BLACK))
        } else if (status == "dead") {
            game.addMark(DeadMark(point))
        }
    }

    fun addStone(color: StoneColor, point: Point) {
        command("play ${color.toString().toLowerCase()} ${point}", null)
    }

    override fun stoneChanged(point: Point) {
        // Ignore the stones that I generated
        if (point != generatedPoint) {
            val color = game.board.getStoneAt(point)
            if (color == StoneColor.NONE) {
                // TODO Remove the stone
            } else {
                addStone(color, point)
            }
        }
    }

    fun countGame() {
        command("2 final_score", null)

        for (y in 0..game.board.size - 1) {
            for (x in 0..game.board.size - 1) {
                val point = Point(x, y)
                val pointNumber = x + y * game.board.size
                command("3${pointNumber} final_status ${point}", null)
            }
        }
    }

    fun tidyUp() {
        game.listeners.remove(this)
        exec.kill()
    }

}

interface GnuGoClient {

    fun generatedMove(point: Point) {}
    fun generatedPass() {}
    fun generatedResign() {}
}

