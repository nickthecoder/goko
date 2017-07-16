package uk.co.nickthecoder.kogo.gui

import javafx.scene.Node
import uk.co.nickthecoder.kogo.model.Board
import uk.co.nickthecoder.kogo.model.Point
import uk.co.nickthecoder.kogo.util.array2d

class MarksViewArray(board: Board) {

    val marksView = MarksView(board)

    val node: Node
        get() = marksView.node

    val array = array2d<MarkView?>(board.sizeX, board.sizeY) { null }

    fun add(markView: MarkView) {
        if (!marksView.board.contains(markView.point)) {
            throw IllegalArgumentException("Not within the board")
        }
        val oldMark = array[markView.point.x][markView.point.y]
        oldMark?.let { marksView.remove(it) }
        array[markView.point.x][markView.point.y] = markView
        marksView.add(markView)
    }

    fun removeAt(point: Point) {
        val mark = array[point.x][point.y]
        mark?.let { marksView.remove(it) }
    }
}
