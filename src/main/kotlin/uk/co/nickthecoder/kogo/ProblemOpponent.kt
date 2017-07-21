package uk.co.nickthecoder.kogo

import uk.co.nickthecoder.kogo.gui.ProblemView
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.Point
import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.kogo.preferences.Preferences

/**
 * Automatically play the opponents moves when solving Go problems
 */
class ProblemOpponent(val game: Game, override val color: StoneColor, val problemView: ProblemView) : Player {

    override val label = "Opponent"

    override val rank = ""

    override fun yourTurn() {
        if (Preferences.problemsAutomaticOpponent == true) {
            val currentNode = game.currentNode
            val nextNode = currentNode.children.firstOrNull()

            nextNode?.let {
                it.apply(game, this)
            }
        }
    }

    override fun makeMove(point: Point) {
        game.move(point, color, this)
    }

    override fun pass() {
        game.pass(this)
    }

    override fun canClickToPlay() : Boolean {
        if (game.currentNode.children.isEmpty()) {
            return true
        }
        return false
    }
}
