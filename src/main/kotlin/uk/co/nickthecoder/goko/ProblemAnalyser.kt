package uk.co.nickthecoder.goko

import javafx.application.Platform
import uk.co.nickthecoder.goko.model.Board
import uk.co.nickthecoder.goko.model.Game
import uk.co.nickthecoder.goko.model.Point
import uk.co.nickthecoder.goko.model.StoneColor

/**
 * Uses GnuGo to give hints for Go Problems.
 * It's pretty crap though, it couldn't even solve some of the easy ones, so I'm not using it at the momement.
 */
class ProblemAnalyser(val game: Game, val hintHandler: (Point) -> Unit) : GnuGoClient {

    val board: Board
        get() = game.board

    val blackPoint: Point?
    val whitePoint: Point?

    var isBlackSurrounded: Boolean? = null
    var isWhiteSurrounded: Boolean? = null

    init {

        blackPoint = findCenterStone(StoneColor.BLACK)
        whitePoint = findCenterStone(StoneColor.WHITE)

        println("Center Black = $blackPoint")
        println("Center Black = $whitePoint")

        if (blackPoint != null && whitePoint != null) {
            game.createGnuGo().isSurrounded(blackPoint, this)
            game.createGnuGo().isSurrounded(whitePoint, this)
        }
    }

    override fun surroundedResults(point: Point, value: Int) {
        if (blackPoint == null || whitePoint == null) {
            return
        }

        println("surroundedResults = $value for $point")

        if (point == blackPoint) {
            isBlackSurrounded = value > 0
        }

        if (point == whitePoint) {
            isWhiteSurrounded = value > 0
        }
        println("BS ? $isBlackSurrounded")
        println("WS ? $isWhiteSurrounded")

        if (isBlackSurrounded == false && isWhiteSurrounded == false) {
            // Hmm, lets see who's closer to a corner (furthest away from the center)
            val o = (board.size - 1) / 2
            val diffB = (o - blackPoint.x) * (o - blackPoint.x) + (o - blackPoint.y) * (o - blackPoint.y)
            val diffW = (o - whitePoint.x) * (o - whitePoint.x) + (o - whitePoint.y) * (o - whitePoint.y)
            isBlackSurrounded = diffB > diffW
            isWhiteSurrounded = diffW > diffB

            println("DiffB $diffB, DiffW $diffW")
            println("BS ? $isBlackSurrounded")
            println("WS ? $isWhiteSurrounded")
        }

    }

    fun hint() {
        if (blackPoint == null || whitePoint == null) {
            return
        }
        val gnuGo = game.createGnuGo()
        if (game.playerToMove.color == StoneColor.BLACK) {
            if (isBlackSurrounded == true || isWhiteSurrounded == false) {
                println("Defend black")
                gnuGo.defend(blackPoint, this)
            } else {
                println("Attack white")
                gnuGo.attack(whitePoint, this)
            }

        } else {
            if (isWhiteSurrounded == true || isBlackSurrounded == false) {
                println("Defend white")
                gnuGo.defend(whitePoint, this)
            } else {
                println("Attack black")
                gnuGo.attack(blackPoint, this)
            }
        }
    }

    override fun attackOrDefend(point: Point?) {
        if (point != null) {
            Platform.runLater {
                hintHandler(point)
            }
        }
    }

    fun findCenterStone(color: StoneColor): Point? {
        var xSum: Double = 0.0
        var ySum: Double = 0.0
        var count: Int = 0
        val points = mutableListOf<Point>()

        for (y in 0..board.size - 1) {
            for (x in 0..board.size - 1) {
                if (board.getStoneAt(x, y) == color) {
                    count++
                    xSum++
                    ySum++
                    points.add(Point(x, y))
                }
            }
        }

        val centerX = xSum / count
        val centerY = ySum / count

        var closest: Point? = null
        var bestDist = Double.MAX_VALUE
        points.forEach { point ->
            val diff2 = (point.x - centerX) * (point.x - centerX) + (point.y - centerY) * (point.y - centerY)
            if (diff2 < bestDist) {
                bestDist = diff2
                closest = point
            }
        }
        return closest
    }
}

