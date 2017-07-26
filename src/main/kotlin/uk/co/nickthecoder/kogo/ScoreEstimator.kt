package uk.co.nickthecoder.kogo

import javafx.application.Platform
import uk.co.nickthecoder.kogo.model.AlternateMark
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.Point

/**
 */
class ScoreEstimator(val game: Game) : GnuGoClient {

    private val node = game.currentNode

    private lateinit var callback: (String) -> Unit

    fun estimate(callback: (String) -> Unit) {
        this.callback = callback
        game.createGnuGo().estimateScore(this)
    }

    override fun scoreEstimate(score: String) {
        Platform.runLater {
            callback(score)
        }
    }

}
