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
package uk.co.nickthecoder.goko.gui

import javafx.application.Platform
import uk.co.nickthecoder.goko.model.Game
import uk.co.nickthecoder.goko.model.GameNode
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription

/**
 */
class DeleteBranchTask(val game: Game) : AbstractTask() {

    override val taskD = TaskDescription("Delete Branch", description = """
Delete this whole branch or the game tree,
which contains ${countNodes(game.currentNode)} nodes?
""")

    val node = game.currentNode

    override fun run() {
        if (game.currentNode === node) {
            Platform.runLater {
                game.deleteBranch()
            }
        }
    }
}

private fun countNodes(node: GameNode): Int {
    return 1 + node.children.map {
        countNodes(it)
    }.sum()
}
