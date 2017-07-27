package uk.co.nickthecoder.kogo

import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.GameListener
import uk.co.nickthecoder.kogo.model.Point
import uk.co.nickthecoder.kogo.model.StoneColor
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

    private val replyHandlers = Collections.synchronizedMap(HashMap<Int, ReplyHandler>())

    var commandNumber = 1

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
        val handler = MoveHandler(client)
        command("genmove ${color.toString().toLowerCase()}", handler)
    }

    fun addStone(color: StoneColor, point: Point) {
        command("play ${color.toString().toLowerCase()} ${point}", null)
    }


    /**
     * Generate a move, but do not acturally play it. Used to generate Hints.
     */
    fun generateHint(color: StoneColor, client: GnuGoClient) {
        val handler = MoveHandler(client)
        command("reg_genmove ${color.toString().toLowerCase()}", handler)
    }

    fun estimateScore(client: GnuGoClient) {
        for (y in 0..game.board.size - 1) {
            for (x in 0..game.board.size - 1) {
                val point = Point(x, y)
                val handler = PointStatusHandler(client, point)
                command("final_status ${point}", handler)
            }
        }
        val handler = FinalScoreHandler(client)
        command("final_score", handler)
    }

    @Synchronized
    private fun command(command: String, handler: ReplyHandler?) {
        val number = commandNumber++
        replyHandlers.put(number, handler)

        println("Sending command : '$number $command'")
        writer?.let {
            it.appendln("$number $command")
            it.flush()
        }
    }

    private fun parseLine(line: String) {

        if (!line.startsWith("=")) {
            return
        }

        val space = line.indexOf(' ')
        if (space > 0) {
            val reply = line.substring(space + 1)
            val number = line.substring(1, space).toInt()
            val handler = replyHandlers.remove(number)
            println("Paring '$reply' with handler $handler")
            handler?.parseReply(reply)
        }

    }

    override fun stoneChanged(point: Point) {
        val color = game.board.getStoneAt(point)
        if (color == StoneColor.NONE) {
            // TODO Remove the stone
        } else {
            addStone(color, point)
        }
    }

    fun tidyUp() {
        game.listeners.remove(this)
        exec.kill()
    }

}

abstract class ReplyHandler(val client: GnuGoClient) {

    abstract fun parseReply(reply: String)
}

class MoveHandler(client: GnuGoClient) : ReplyHandler(client) {

    override fun parseReply(reply: String) {
        if (reply == "resign") {
            client.generatedResign()
            return
        }
        if (reply == "PASS") {
            client.generatedPass()
            return
        }
        val point = Point.fromString(reply)
        client.generatedMove(point)
    }
}

class PointStatusHandler(client: GnuGoClient, val point: Point) : ReplyHandler(client) {

    override fun parseReply(reply: String) {
        client.pointStatus(point, reply)
    }
}

class FinalScoreHandler(client: GnuGoClient) : ReplyHandler(client) {

    override fun parseReply(reply: String) {
        client.scoreEstimate(reply)
    }
}

interface GnuGoClient {
    fun generatedMove(point: Point) {}
    fun generatedPass() {}
    fun generatedResign() {}
    fun scoreEstimate(score: String) {}
    fun pointStatus(point: Point, status: String) {}
}