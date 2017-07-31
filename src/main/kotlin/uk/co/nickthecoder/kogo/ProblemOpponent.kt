package uk.co.nickthecoder.kogo

import uk.co.nickthecoder.kogo.gui.ProblemView
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.paratask.util.runLater

/**
 * Automatically play the opponents moves when solving Go problems
 */
class ProblemOpponent(val game: Game, override val color: StoneColor, val problemView: ProblemView) : Player {

    override var label = "Opponent"

    override var timeRemaining: TimeLimit = NoTimeLimit()

    override val rank = ""

    override fun yourTurn() {
        if (Preferences.problemsAutomaticOpponent == true) {
            val currentNode = game.currentNode
            val nextNode = currentNode.children.firstOrNull()

            nextNode?.let {
                runLater(500) {
                    it.apply(game)
                    KoGo.stoneSound()
                }
            }
        }
    }

    override fun makeMove(point: Point) {
        game.move(point, color)
    }

    override fun pass() {
        game.pass()
    }

    override fun canClickToPlay(): Boolean {
        if (game.currentNode.children.isEmpty()) {
            return true
        }
        return false
    }
}
