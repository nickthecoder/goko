package uk.co.nickthecoder.kogo

import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.StoneColor

/**
 * Automatically play the opponents moves when solving Go problems
 */
class ProblemPlayer(val game: Game, override val color: StoneColor) : Player {

    override val label = "Opponent"

    override val rank = ""

    override fun yourTurn() {
        val currentNode = game.currentNode
        val nextNode = currentNode.children.firstOrNull()

        nextNode?.let {
            it.apply(game, this)
        }
    }
}
