package uk.co.nickthecoder.kogo.gui

import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Parent
import javafx.scene.canvas.Canvas
import javafx.scene.effect.BoxBlur
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import uk.co.nickthecoder.kogo.model.Board
import uk.co.nickthecoder.kogo.model.Point


class BoardView(val board: Board) {

    val playingArea: PlayingArea = PlayingArea(this)

    private val boardNode = StackPane()

    private val lines = Canvas((board.sizeX) * BoardView.pointSize, (board.sizeY) * BoardView.pointSize)

    private val boardMarks = MarksView(board)

    private val scaledBoard = ScaledBoard()

    val node: Parent
        get() = scaledBoard

    init {
        with(scaledBoard) {
            styleClass.add("board-container")
        }

        with(boardNode) {
            styleClass.add("board")
            drawLines()
            children.addAll(lines, boardMarks.node, playingArea.node)
        }

        for (x in 0..board.sizeX - 1) {
            val text = Point.labelX(x)
            val bottomLabel = MarkView(Point(x, -1), "coordinate", text)
            val topLabel = MarkView(Point(x, board.sizeY), "coordinate", text)
            boardMarks.add(topLabel)
            boardMarks.add(bottomLabel)
        }

        for (y in 0..board.sizeY - 1) {
            val text = Point.labelY(y)
            val leftLabel = MarkView(Point(-1, y), "coordinate", text)
            val rightLabel = MarkView(Point(board.sizeX, y), "coordinate", text)
            boardMarks.add(leftLabel)
            boardMarks.add(rightLabel)
        }

        if (board.sizeX == 19 && board.sizeY == 19) {
            starMarks(3, 3, 6)
        } else if (board.sizeX == 13 && board.sizeY == 13) {
            starMarks(3, 3, 6)
        } else if (board.sizeX == 9 && board.sizeY == 9) {
            starMarks(2, 3, 2)
        }
    }

    fun toBoardPoint(x: Double, y: Double): Point {
        return Point((x / pointSize).toInt(), board.sizeY - (y / pointSize).toInt() - 1)
    }

    fun starMarks(start: Int, n: Int, spacing: Int) {
        for (x in 0..n - 1) {
            for (y in 0..n - 1) {
                val star = SymbolMark(Point(start + spacing * x, start + spacing * y), style = "star")
                boardMarks.add(star)
            }
        }
    }

    fun drawLines() {
        val gc = lines.getGraphicsContext2D()
        val lineWidth = 4.0
        val blurSize = lineWidth * 2

        val blur = BoxBlur()
        blur.setWidth(blurSize)
        blur.setHeight(blurSize)
        blur.setIterations(1)
        gc.setEffect(blur)

        gc.stroke = Color.BLACK
        gc.fill = Color.BLACK
        gc.lineWidth = lineWidth
        for (x in 0..board.sizeX - 1) {
            gc.strokeLine(
                    (x + 0.5) * BoardView.pointSize, 0.5 * BoardView.pointSize,
                    (x + 0.5) * BoardView.pointSize, (board.sizeY - 0.5) * BoardView.pointSize)
        }
        for (y in 0..board.sizeY - 1) {
            gc.strokeLine(
                    0.5 * BoardView.pointSize, (y + 0.5) * BoardView.pointSize,
                    (board.sizeY - 0.5) * BoardView.pointSize, (y + 0.5) * BoardView.pointSize)
        }
    }

    companion object {
        val pointSize = 128.0
    }

    inner class ScaledBoard : Region() {

        init {
            children.add(boardNode)
        }

        override fun layoutChildren() {
            val w = (board.sizeX + 2) * BoardView.pointSize
            val h = (board.sizeY + 2) * BoardView.pointSize
            val xScale = (width - insets.left - insets.right) / w
            val yScale = (height - insets.top - insets.bottom) / h
            val scale = Math.min(xScale, yScale)
            val x = insets.left
            val y = insets.top

            val transX = (width - w) / 2.0 - insets.top
            val transY = (height - h) / 2.0 - insets.top
            with(boardNode) {
                translateX = transX
                translateY = transY
                scaleX = scale
                scaleY = scale
            }
            layoutInArea(boardNode, x, y, w, h, 0.0, HPos.CENTER, VPos.CENTER)
            super.layoutChildren()
        }
    }

}
