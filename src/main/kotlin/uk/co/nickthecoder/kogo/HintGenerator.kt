package uk.co.nickthecoder.kogo

import javafx.application.Platform
import uk.co.nickthecoder.kogo.model.AlternateMark
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.Point

/**
 */
class HintGenerator(val game: Game) : GnuGoClient {

    val node = game.currentNode

    fun hint() {
        game.createGnuGo().generateHint(node.colorToPlay, this)
    }

    override fun generatedMove(point: Point) {
        if (game.currentNode == node) {
            Platform.runLater {
                game.addMark(AlternateMark(point))
            }
        }
    }
}
