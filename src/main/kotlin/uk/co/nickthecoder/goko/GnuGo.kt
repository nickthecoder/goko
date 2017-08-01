/*
GoKo a Go Client
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.goko

import uk.co.nickthecoder.goko.model.*
import uk.co.nickthecoder.paratask.util.process.BufferedSink
import uk.co.nickthecoder.paratask.util.process.Exec
import java.io.OutputStreamWriter
import java.io.Writer
import java.util.*

/**
 * An interface to GnuGo's Process. One instance can be shared my multiple clients, for example, one client may be a
 * GnuGoPlayer, and another is used to create topMoves or calculate the score at the end of the game.
 *
 * Uses the GTP protocal, running in a separate process. Therefore the results from a command are not returned immediatly,
 * instead the results are passed back via GnuGoClient.
 */
class GnuGo(val game: Game, level: Int) : GameListener {

    private val exec = Exec("gnugo", "--mode", "gtp", "--level", level, "--boardsize", game.board.size, "--komi", game.metaData.komi)

    private var writer: Writer? = null

    private val replyHandlers = Collections.synchronizedMap(HashMap<Int, ReplyHandler>())

    private var myNode: GameNode? = null

    private var commandNumber = 1

    private var currentHandler: ReplyHandler? = null

    private var generatedMove = false

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

        syncBoard()
    }

    fun placeHandicap(client: GnuGoClient) {
        val handler = PlaceHandicapHandler(client)
        command("place_free_handicap ${game.metaData.handicap}", handler)
    }

    fun generateMove(color: StoneColor, client: GnuGoClient) {
        val handler = MoveHandler(client)
        command("genmove ${color.toString().toLowerCase()}", handler)
        generatedMove = true
    }

    fun addStone(color: StoneColor, point: Point) {
        command("play ${color.toString().toLowerCase()} $point", null)
    }

    fun undo() {
        command("undo", null)
    }

    fun influence(type: String, client: GnuGoClient, color: StoneColor) {
        command("initial_influence ${color.toString().toLowerCase()} $type", InfluenceHandler(client, this))
    }

    fun syncBoard() {
        command("clear_board", null)
        for (y in 0..game.board.size - 1) {
            for (x in 0..game.board.size - 1) {
                val color = game.board.getStoneAt(x, y)
                if (color.isStone()) {
                    addStone(color, Point(x, y))
                }
            }
        }
        myNode = game.currentNode
        println("GnuGo. Sync complete")
    }

    /**
     * Compares GnuGo's version of the board with mine, and reports any inconsistancies on the console.
     */
    fun checkBoard() {
        val handlerB = CheckBoardHandler(game, StoneColor.BLACK)
        command("list_stones black", handlerB)
        val handlerW = CheckBoardHandler(game, StoneColor.WHITE)
        command("list_stones white", handlerW)
    }

    fun topMoves(color: StoneColor, client: GnuGoClient) {
        // The top_moves command doesn't work if gnuGo hasn't generated a move, so lets create one, and undo it!
        // Otherwise we will only be able to get hints when playing against GnuGo.
        if (!generatedMove) {
            command("genmove black", null)
            command("undo", null)
        }
        val handler = TopMovesHandler(client)
        command("top_moves ${color.toString().toLowerCase()}", handler)
    }

    fun estimateScore(client: GnuGoClient) {
        command("estimate_score", EstimateScoreHandler(client))
    }

    fun finalScore(client: GnuGoClient) {
        for (y in 0..game.board.size - 1) {
            for (x in 0..game.board.size - 1) {
                val point = Point(x, y)
                val handler = PointStatusHandler(client, point)
                command("final_status $point", handler)
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

        println("Parsing line : '$line'")
        var data: String

        if (line.startsWith("=")) {
            data = line.substring(1)
            val space = data.indexOf(' ')
            val number: Int
            if (space >= 0) {
                number = data.substring(0, space).toInt()
                data = data.substring(space + 1).trim()
            } else {
                number = data.trim().toInt()
                data = ""
            }
            currentHandler = replyHandlers.remove(number)
        } else {
            if (line.isBlank()) {
                currentHandler = null
                return
            }
            data = line
        }

        if (data.isNotBlank()) {
            println("Parsing '$data' with handler $currentHandler")
            currentHandler?.parseReply(data)
        }
    }

    override fun stoneChanged(point: Point) {
        if (game.currentNode is SetupNode) {
            val color = game.board.getStoneAt(point)
            if (color.isStone()) {
                addStone(color, point)
            } else {
                // There is no way to remove a stone from GnuGo's board, so lets just clear it and start afresh.
                syncBoard()
            }
        }
    }

    override fun madeMove(gameNode: GameNode) {
        if (gameNode is MoveNode) {
            if (gameNode.parent != myNode) {
                syncBoard()
            } else {
                addStone(gameNode.color, gameNode.point)
                myNode = gameNode
            }
        } else if (gameNode is SetupNode) {
            syncBoard()
        }
    }

    override fun undoneMove(gameNode: GameNode) {
        if (myNode === gameNode) {
            undo()
            myNode = gameNode.parent
        } else {
            syncBoard()
        }
    }

    fun tidyUp() {
        game.listeners.remove(this)
        exec.kill()
    }

}

abstract class ReplyHandler(val client: GnuGoClient?) {

    abstract fun parseReply(reply: String)
}

private class MoveHandler(client: GnuGoClient?) : ReplyHandler(client) {

    override fun parseReply(reply: String) {
        if (reply == "resign") {
            client?.generatedResign()
            return
        }
        if (reply == "PASS") {
            client?.generatedPass()
            return
        }
        val point = Point.fromString(reply)
        client?.generatedMove(point)
    }
}

private class TopMovesHandler(client: GnuGoClient) : ReplyHandler(client) {

    override fun parseReply(reply: String) {
        if (reply.isNotBlank()) {
            val pairList = reply.trim().split(' ').withIndex().groupBy { it.index / 2 }.map { it.value.map { it.value } }
            val results = pairList.map { Pair(Point.fromString(it[0]), it[1].toDouble()) }

            client?.topMoves(results)
        } else {
            client?.topMoves(listOf())
        }
    }
}

private class CheckBoardHandler(val game: Game, val color: StoneColor) : ReplyHandler(null) {

    override fun parseReply(reply: String) {
        if (reply.isNotBlank()) {
            val points = reply.trim().split(' ').map { Point.fromString(it) }

            for (y in 0..game.board.size - 1) {
                for (x in 0..game.board.size - 1) {
                    val point = Point(x, y)
                    val gokosColor = game.board.getStoneAt(x, y)
                    if (color == gokosColor) {
                        if (!points.contains(point)) {
                            println("Point $point is $color on my board, but not on GnuGo's")
                        }
                    }
                    if (gokosColor == StoneColor.NONE) {
                        if (points.contains(point)) {
                            println("Point $point is EMPTY on my board, but is $color on GnuGo's")
                        }
                    }
                }
            }
        }
    }
}

private class PlaceHandicapHandler(client: GnuGoClient) : ReplyHandler(client) {

    override fun parseReply(reply: String) {
        reply.split(" ").forEach {
            val point = Point.fromString(it)
            client?.generatedMove(point)
        }
    }
}

private class PointStatusHandler(client: GnuGoClient, val point: Point) : ReplyHandler(client) {

    override fun parseReply(reply: String) {
        client?.pointStatus(point, reply)
    }
}

private class FinalScoreHandler(client: GnuGoClient) : ReplyHandler(client) {

    override fun parseReply(reply: String) {
        client?.finalScoreResults(reply)
    }
}

private class EstimateScoreHandler(client: GnuGoClient) : ReplyHandler(client) {

    override fun parseReply(reply: String) {
        client?.estimateScoreResults(reply)
    }
}


private class InfluenceHandler(client: GnuGoClient, val gnuGo: GnuGo) : ReplyHandler(client) {

    var y = 0
    val results = MutableList(gnuGo.game.board.size, { MutableList(gnuGo.game.board.size, { 0.0 }) })

    override fun parseReply(reply: String) {
        val values = reply.trim().split(" ").filter { it.isNotBlank() }.map { it.toDouble() }
        for (i in 0..gnuGo.game.board.size - 1) {
            results[i][gnuGo.game.board.size - y - 1] = values[i]
        }
        y++
        if (y == gnuGo.game.board.size) {
            client?.influenceResults(results)
        }
    }
}
