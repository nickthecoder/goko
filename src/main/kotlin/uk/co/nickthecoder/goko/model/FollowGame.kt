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
 * Makes a copy of a game, and follows the original, any moves added to the original's main line are added
 * to the copy too.
 * This can be used to edit a game while it is still in progress, so you can review a game while still in
 * progress. You are free to add branches to the copy. However only the main-line in the original are
 * followed. Therefore, if a move is undone in the original, in order for the copy to keep updating, then
 * the new branch must be added as the main-line (i.e. the first child node).
 */
class FollowGame(val original: Game) : GameListener {

    val copy: Game = original.copy()

    init {
        original.listeners.add(this)
    }

    override fun madeMove(gameNode: GameNode) {
        val nodeIndex = mainLineNodeIndex(original.currentNode)
        nodeIndex ?: return

        var n: GameNode = copy.root
        for (i in 0..nodeIndex - 2) {
            n = n.children[0]
        }
        val newNode = original.currentNode.copy()
        n.children.add(0, newNode)
        newNode.parent = n
    }

    fun mainLineNodeIndex(node: GameNode): Int? {
        var n: GameNode? = node
        var index = -1
        while (n != null) {
            index++
            if (n.parent == null) {
                return index
            }
            if (n.parent?.children?.get(0) != n) {
                return null
            }
            n = n.parent
        }
        return null
    }
}
