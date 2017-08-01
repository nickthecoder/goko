package uk.co.nickthecoder.goko

import uk.co.nickthecoder.goko.model.*
import uk.co.nickthecoder.goko.preferences.Preferences
import uk.co.nickthecoder.paratask.util.runLater

/**
 * Automatically play the opponents moves when solving Go problems
 */
class ProblemOpponent(val game: Game, override val color: StoneColor) : Player {

    override var label = "Opponent"

    override var timeRemaining: TimeLimit = NoTimeLimit()

    override val rank = ""

    override fun yourTurn() {
        if (Preferences.problemsAutomaticOpponent == true) {
            val currentNode = game.currentNode
            val nextNode = currentNode.children.firstOrNull()

            nextNode?.let {
                runLater(500) {
                    game.apply(it)
                    GoKo.stoneSound()
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
