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

import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.goko.GoKo
import uk.co.nickthecoder.goko.model.Board
import uk.co.nickthecoder.goko.model.Game
import uk.co.nickthecoder.goko.model.GameListener
import uk.co.nickthecoder.goko.model.Point

class ClickBoardView(val boardView: BoardView) : GameListener {

    private val stack = StackPane()

    val node: Node
        get() = stack

    val board: Board
        get() = boardView.board

    val game: Game
        get() = boardView.board.game

    var onClickedPoint: (Point) -> Unit = { point ->
        val player = game.playerToMove

        if (player.canClickToPlay() && game.canPlayAt(point)) {
            player.makeMove(point)
            GoKo.stoneSound()
        }
    }

    init {
        with(stack) {
            styleClass.add("playing-area")
            prefWidth = boardView.board.size * BoardView.pointSize
            prefHeight = boardView.board.size * BoardView.pointSize
        }

        stack.addEventHandler(MouseEvent.MOUSE_MOVED) { onMouseMoved(it) }
        stack.addEventHandler(MouseEvent.MOUSE_CLICKED) { onMouseClicked(it) }
        stack.addEventHandler(MouseEvent.MOUSE_EXITED) { onMouseExited() }

        game.listeners.add(this)

    }

    fun onMouseMoved(event: MouseEvent) {
        val point = boardView.toBoardPoint(event.x, event.y)
        if (board.contains(point)) {
            boardView.mouseMarkAt(point)
        } else {
            boardView.mouseMarkAt(null)
        }
    }

    fun onMouseExited() {
        boardView.mouseMarkAt(null)
    }

    fun onMouseClicked(event: MouseEvent) {
        node.requestFocus()
        val point = boardView.toBoardPoint(event.x, event.y)
        if (board.contains(point)) {
            onClickedPoint(point)
        }
    }

}
