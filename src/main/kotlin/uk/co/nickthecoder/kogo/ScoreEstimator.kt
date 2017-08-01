package uk.co.nickthecoder.kogo

import javafx.application.Platform
import uk.co.nickthecoder.kogo.model.*

/**
 */
class ScoreEstimator(val game: Game) : GnuGoClient {

    private lateinit var callback: (String) -> Unit

    private val marks = mutableListOf<Mark>()

    fun estimate(callback: (String) -> Unit) {
        this.callback = callback
        game.createGnuGo().estimateScore(this)
    }

    override fun scoreEstimate(score: String) {
        Platform.runLater {
            //if (node == game.currentNode) {
                callback(score)
                for (mark in marks) {
                    game.addMark(mark)
                }
            //}
            marks.clear()
        }
    }

    override fun pointStatus(point: Point, status: String) {
        // undecided", "alive", "dead", "white_territory", "black_territory
        if (status == "dead") {
            marks.add(DeadMark(point))
        } else if (status == "white_territory") {
            marks.add(TerritoryMark(point, StoneColor.WHITE))
        } else if (status == "black_territory") {
            marks.add(TerritoryMark(point, StoneColor.BLACK))
        } else if (status == "undecided" && game.board.getStoneAt(point).isStone()) {
            marks.add(QuestionMark(point))
        }
    }
}

