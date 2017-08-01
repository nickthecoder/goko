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

import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.layout.Pane
import uk.co.nickthecoder.goko.model.Board
import uk.co.nickthecoder.goko.model.Point
import uk.co.nickthecoder.goko.model.StoneColor

/**
 * Shows a set of marks on the board or on top of stones.
 * Used to mark board positions with squares/triangles/circle, labelling a position with a letter,
 * displaying the latest stone placed, etc.
 */
class MarksView(val board: Board) {

    val markViews = mutableListOf<MarkView>()

    val node = object : Pane() {

        override fun layoutChildren() {
            markViews.forEach { child ->
                val x = child.point.x * BoardView.pointSize
                val y = (board.size - child.point.y - 1) * BoardView.pointSize
                layoutInArea(child, x, y, BoardView.pointSize, BoardView.pointSize, 0.0, HPos.CENTER, VPos.CENTER)
            }
        }
    }

    init {
        node.styleClass.add("marks")
    }

    fun clear() {
        markViews.clear()
        node.children.clear()
    }

    fun add(markView: MarkView, color: StoneColor? = null) {
        if (color == null) {
            val stone = board.getStoneAt(markView.point)
            if (stone != StoneColor.NONE) {
                markView.colorWhite(stone == StoneColor.BLACK)
            }
        } else {
            markView.colorWhite(color == StoneColor.WHITE)
        }
        markViews.add(markView)
        markView.marksView = this
        node.children.add(markView)
    }

    fun remove(point: Point) {
        for (mv in markViews) {
            if (mv.point == point) {
                remove(mv)
                return
            }
        }
    }

    fun remove(markView: MarkView) {
        markViews.remove(markView)
        markView.marksView = null
        node.children.remove(markView)
    }
}
