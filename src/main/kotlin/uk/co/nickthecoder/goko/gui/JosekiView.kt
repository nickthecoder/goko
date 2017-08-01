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

import javafx.scene.control.SplitPane
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.goko.model.AlternateMark
import uk.co.nickthecoder.goko.model.GameNode
import uk.co.nickthecoder.goko.model.MoveNode
import uk.co.nickthecoder.goko.model.SGFReader
import uk.co.nickthecoder.goko.preferences.Preferences
import java.io.File

class JosekiView(mainWindow: MainWindow, josekiDatabase: File)
    : AbstractGoView(mainWindow, SGFReader(josekiDatabase).read()) {

    override val title = "Joseki"

    private val split = SplitPane()

    private val rightPane = BorderPane()

    private val commentsView = CommentsView(game, true, Preferences.josekiPreferences)

    override fun build() {
        super.build()
        boardView.build()

        with(rightPane) {
            center = commentsView.node
        }

        whole.center = split

        with(split) {
            items.add(boardView.node)
            items.add(rightPane)
            split.dividers[0].position = 0.7
        }
        commentsView.build()

        toolBar.items.addAll(passB, restartB, backB, forwardB)

        // TODO Is this really needed?
        game.apply(game.root)
    }

    override fun tidyUp() {
        super.tidyUp()
        game.tidyUp()
        boardView.tidyUp()
        commentsView.tidyUp()
    }

    fun update() {
        val currentNode = game.currentNode
        boardView.branches.clear()

        for (child in currentNode.children) {
            if (child is MoveNode) {
                if (!currentNode.hasMarkAt(child.point)) {
                    val mark = AlternateMark(child.point)
                    boardView.branches.add(SymbolMarkView(mark))
                }
            }
        }
    }

    override fun madeMove(gameNode: GameNode) {
        super.madeMove(gameNode)
        update()
    }

    override fun undoneMove(gameNode: GameNode) {
        super.undoneMove(gameNode)
        update()
    }

}
