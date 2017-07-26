package uk.co.nickthecoder.kogo

import javafx.application.Platform
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.paratask.util.process.BufferedSink
import uk.co.nickthecoder.paratask.util.process.Exec
import java.io.OutputStreamWriter
import java.io.Writer

/**
 * Interfaces with the GnuGo AI using gtp mode
 */
class GnuGoPlayer(val game: Game, override val color: StoneColor, level: Int = 10) : Player, GameListener {

    override val label = "Gnu Go level ${level}"

    override val rank = ""

    override var timeRemaining: TimeLimit = NoTimeLimit.instance

    val rules = if (game.metaData.japaneseRules) "japanese" else "chinese"
    val exec = Exec("gnugo", "--mode", "gtp", "--level", level, "--boardsize", game.board.size, "--komi", game.metaData.komi, "--$rules-rules")

    var writer: Writer? = null

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

    override fun yourTurn() {
        command("1 genmove ${color.toString().toLowerCase()}")
    }

    private fun command(command: String) {
        writer?.let {
            println("Sending command '$command'")
            it.appendln(command)
            it.flush()
        }
    }

    private fun parseLine(line: String) {
        if (line == "" || line == "= ") {
            return
        }
        println("Parsing reply '$line'")

        if (line.startsWith("=1 ") && line.length >= 5) {
            if (line.startsWith("=1 resign")) {
                game.resign(this)
                return
            }
            if (line.startsWith("=1 PASS")) {
                Platform.runLater {
                    game.pass(this)
                }
                return
            }
            val point = Point.fromString(line.substring(3))
            Platform.runLater {
                game.move(point, color, this)
            }

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

    override fun stoneChanged(point: Point, byPlayer: Player?) {
        // Ignore the stones that I generated
        if (byPlayer != this) {
            val color = game.board.getStoneAt(point)
            if (color == StoneColor.NONE) {
                // TODO Remove the stone
            } else {
                command("play ${color.toString().toLowerCase()} ${point}")
            }
        }
    }

    fun countGame() {
        command("2 final_score")

        for (y in 0..game.board.size - 1) {
            for (x in 0..game.board.size - 1) {
                val point = Point(x, y)
                val pointNumber = x + y * game.board.size
                command("3${pointNumber} final_status ${point}")
            }
        }
    }

    override fun tidyUp() {
        game.listeners.remove(this)
        exec.kill()
    }

    override fun makeMove(point: Point) {
        throw IllegalStateException("It's GnuGoPlayer's turn!")
    }

    override fun pass() {
        throw IllegalStateException("It's GnuGoPlayer's turn!")
    }

    override fun canClickToPlay() = false
}
