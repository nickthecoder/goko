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

    fun add(markView: MarkView) {
        val stone = board.getStoneAt(markView.point)
        if (stone != StoneColor.NONE) {
            markView.colorWhite(stone == StoneColor.BLACK)
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
