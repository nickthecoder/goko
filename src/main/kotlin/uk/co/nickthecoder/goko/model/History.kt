/*
GoKo a Go Client
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.goko.model

/**
 * Keeps track of the nodes traversed, so that when moving backwards and then forwards again, you move through the
 * the same set of nodes. Moving forwards without moving backwards will pick the first child node.
 */
class History(val game: Game) : GameListener {

    private val history = mutableListOf<GameNode>()

    init {
        game.listeners.add(this)
    }

    fun forward(n: Int = 1) {
        for (foo in 1..n) {
            val i = history.indexOf(game.currentNode)
            if (i >= 0 && i < history.size - 1) {
                val node = history[i + 1]
                if (node.parent == null) {
                    // It has been deleted from the game tree.
                    game.moveForward()
                } else {
                    game.apply(node)
                }
            } else {
                game.moveForward()
            }
        }
    }

    /**
     * Rewind until we are on the main line (i.e. first child nodes only), and then clear the future nodes, so that
     * forward() will continue along the main line.
     */
    fun mainLine() {
        for (node in history) {
            val parent = node.parent
            if (parent != null && parent.children[0] != node) {
                game.rewindTo(parent)
                val i = history.indexOf(game.currentNode)
                if (i >= 0) {
                    while (history.size > i) {
                        history.removeAt(history.size - 1)
                    }
                } else {
                    println("Hmm, something went wrong in History.mainLine")
                    println("Main history = $history")
                }
                println("Main history = $history")
                return
            }
        }
    }

    override fun madeMove(gameNode: GameNode) {

        val c = history.indexOf(gameNode)
        if (c < 0) {
            val p = history.indexOf(gameNode.parent)
            if (p >= 0) {
                while (history.size > p + 1) {
                    history.removeAt(history.size - 1)
                }
                history.add(gameNode)
            } else {
                history.clear()
                var node: GameNode? = gameNode
                while (node != null) {
                    history.add(0, node)
                    node = node.parent
                }
            }
        }
    }
}
