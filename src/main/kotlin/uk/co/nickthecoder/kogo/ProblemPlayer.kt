package uk.co.nickthecoder.kogo

import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.Point
import uk.co.nickthecoder.kogo.model.StoneColor

class ProblemPlayer(game: Game, color: StoneColor) : LocalPlayer(game, color) {

    override fun makeMove(point: Point) {
        val node = game.currentNode
        val count = node.children.size
        super.makeMove(point)
        if (node.children.size != count) {
            game.currentNode.comment = "Hmm, that move's not not part of the solution!"
            game.nodeChanged(game.currentNode)
        }
    }

    override fun pass() {
        val node = game.currentNode
        val count = node.children.size
        super.pass()
        if (node.children.size != count) {
            game.currentNode.comment = "Hmm, You passed? That's not part of the solution!"
            game.nodeChanged(game.currentNode)
        }
    }

    override fun canClickToPlay() = true
}
