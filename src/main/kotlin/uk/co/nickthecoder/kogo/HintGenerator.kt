package uk.co.nickthecoder.kogo

import javafx.application.Platform
import uk.co.nickthecoder.kogo.model.AlternateMark
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.MainLineMark
import uk.co.nickthecoder.kogo.model.Point

/**
 */
class HintGenerator(val game: Game) : GnuGoClient {

    val node = game.currentNode

    fun hint() {
        game.createGnuGo().topMoves(node.colorToPlay, this)
    }

    override fun topMoves(points: List<Pair<Point, Double>>) {
        if (game.currentNode == node) {
            Platform.runLater {
                points.forEach { (point, probability) ->
                    // I think there's a bug in my version of gnugo - it always returns the last played move as
                    // the first element in the list.
                    if (!game.board.getStoneAt(point).isStone()) {
                        if (probability > 25.0) {
                            game.addMark(MainLineMark(point))
                        } else if (probability > 10.0) {
                            game.addMark(AlternateMark(point))
                        }
                    }
                }
            }
        }
    }
}
