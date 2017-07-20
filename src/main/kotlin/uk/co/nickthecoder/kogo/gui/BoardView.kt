package uk.co.nickthecoder.kogo.gui

import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Parent
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.StackPane
import javafx.scene.shape.Circle
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import uk.co.nickthecoder.kogo.Player
import uk.co.nickthecoder.kogo.model.*


class BoardView(val game: Game) : View {

    val board = game.board

    private val container = StackPane()

    val stones = MarksViewArray(board)

    val playerView: PlayerView = PlayerView(this@BoardView)

    val specialMarks = MarksView(board)

    val marks = MarksView(board)

    val mouseMark = SymbolMarkView(Point(-10, -10), "mouse")

    val latestMark = SymbolMarkView(Point(-10, -10), "latest") // Initially off-screen

    override val node: Parent
        get() = container

    private val boardLayout = BoardLayout()

    val OFF_SCREEN = Point(-100, -100)


    override fun build(): View {
        with(container) {
            styleClass.add("board-container")
            children.add(boardLayout)
        }
        boardLayout.build()

        return this
    }

    fun toBoardPoint(x: Double, y: Double): Point {
        return Point((x / pointSize).toInt(), board.sizeY - (y / pointSize).toInt() - 1)
    }

    companion object {
        val pointSize = 128.0
    }

    inner class BoardLayout : Pane(), GameListener {

        val lines = Path()

        val starPoints = Pane()

        private val boardMarks = MarksView(board) // Star points and labels around the board

        private val wood = Region()

        var scale: Double = 1.0

        init {
            board.game.gameListeners.add(this)

            specialMarks.add(mouseMark)
            specialMarks.add(latestMark)
        }

        fun build() {
            children.addAll(wood, lines, starPoints, boardMarks.node, stones.node, marks.node, specialMarks.node, playerView.node)

            with(wood) {
                styleClass.add("wood")
            }

            with(boardMarks) {
                styleClass.add("board-marks")
                for (x in 0..board.sizeX - 1) {
                    val text = Point.labelX(x)
                    val bottomLabel = MarkView(Point(x, -1), "coordinate", text)
                    val topLabel = MarkView(Point(x, board.sizeY), "coordinate", text)
                    add(topLabel)
                    add(bottomLabel)
                }

                for (y in 0..board.sizeY - 1) {
                    val text = Point.labelY(y)
                    val leftLabel = MarkView(Point(-1, y), "coordinate", text)
                    val rightLabel = MarkView(Point(board.sizeX, y), "coordinate", text)
                    add(leftLabel)
                    add(rightLabel)
                }
            }
        }

        override fun layoutChildren() {

            val marginTop: Double
            val marginLeft: Double
            val size: Double

            if (width > height) {
                size = height
                marginLeft = (width - height) / 2
                marginTop = 0.0
            } else {
                size = width
                marginLeft = 0.0
                marginTop = (height - width) / 2
            }
            scale = size / (board.sizeX + 2)

            val playingSize = scale * board.sizeX
            val logicalSize = (board.sizeX + 2) * pointSize

            layoutInArea(wood, marginLeft, marginTop, size, size, 0.0, HPos.LEFT, VPos.TOP)
            layoutInArea(boardMarks.node, marginLeft + scale, marginTop + scale, logicalSize, logicalSize, 0.0, HPos.LEFT, VPos.TOP)
            layoutInArea(starPoints, marginLeft + scale * 1.5, marginTop + scale * 1.5, playingSize, playingSize, 0.0, HPos.LEFT, VPos.TOP)
            layoutInArea(stones.node, marginLeft + scale, marginTop + scale, logicalSize, logicalSize, 0.0, HPos.LEFT, VPos.TOP)
            layoutInArea(marks.node, marginLeft + scale, marginTop + scale, logicalSize, logicalSize, 0.0, HPos.LEFT, VPos.TOP)
            layoutInArea(specialMarks.node, marginLeft + scale, marginTop + scale, logicalSize, logicalSize, 0.0, HPos.LEFT, VPos.TOP)
            layoutInArea(playerView.node, marginLeft + scale, marginTop + scale, logicalSize, logicalSize, 0.0, HPos.LEFT, VPos.TOP)

            with(boardMarks.node) {
                translateX = (size - logicalSize) / 2
                translateY = translateX
                scaleX = scale / pointSize
                scaleY = scaleX
            }

            with(stones.node) {
                translateX = (size - logicalSize) / 2
                translateY = translateX
                scaleX = scale / pointSize
                scaleY = scaleX
            }

            with(marks.node) {
                translateX = (size - logicalSize) / 2
                translateY = translateX
                scaleX = scale / pointSize
                scaleY = scaleX
            }

            with(specialMarks.node) {
                translateX = (size - logicalSize) / 2
                translateY = translateX
                scaleX = scale / pointSize
                scaleY = scaleX
            }

            with(playerView.node) {
                translateX = (size - logicalSize) / 2
                translateY = translateX
                scaleX = scale / pointSize
                scaleY = scaleX
            }

            lines.strokeWidth = scale / 70.0

            lines.elements.clear()
            for (x in 0..board.sizeX - 1) {
                val from = MoveTo((x + 0.5) * scale - 1, 0.5 * scale - 1)
                val to = LineTo((x + 0.5) * scale - 1, (board.sizeY - 0.5) * scale - 1)
                lines.elements.addAll(from, to)
            }
            for (y in 0..board.sizeY - 1) {
                val from = MoveTo(0.5 * scale - 1, (y + 0.5) * scale - 1)
                val to = LineTo((board.sizeX - 0.5) * scale - 1, (y + 0.5) * scale - 1)
                lines.elements.addAll(from, to)
            }

            if (board.sizeX == 19 && board.sizeY == 19) {
                starPoints(3, 3, 6)
            } else if (board.sizeX == 13 && board.sizeY == 13) {
                starPoints(3, 3, 6)
            } else if (board.sizeX == 9 && board.sizeY == 9) {
                starPoints(3, 2, 2, cross = true)
            }

            layoutInArea(lines, marginLeft + scale * 1.5, marginTop + scale * 1.5, playingSize, playingSize, 0.0, HPos.LEFT, VPos.TOP)
        }

        fun starPoints(n: Int, start: Int, spacing: Int, cross: Boolean = false) {
            starPoints.children.clear()
            for (x in 0..n - 1) {
                for (y in 0..n - 1) {
                    if (!cross || ((x + y) % 2 == 0)) {
                        val circle = Circle((start + x * spacing) * scale, (start + y * spacing) * scale, scale / 10)
                        starPoints.children.add(circle)
                    }
                }
            }
        }


        override fun stoneChanged(point: Point, byPlayer: Player?) {
            val color = board.getStoneAt(point)
            if (color == StoneColor.NONE) {
                stones.removeAt(point)
            } else {
                stones.add(SymbolMarkView(point, "stone" + if (color == StoneColor.WHITE) "W" else "B"))
            }
        }

        override fun moved() {
            mouseMark.colorWhite(game.playerToMove.color == StoneColor.WHITE)

            val currentNode = board.game.currentNode
            if (currentNode is MoveNode) {
                latestMark.point = currentNode.point
                latestMark.colorWhite(currentNode.color == StoneColor.BLACK)
            } else {
                latestMark.point = OFF_SCREEN
            }
            marks.clear()
            for (mark in board.game.currentNode.marks) {
                marks.add(mark.createMarkView())
            }
        }

        override fun addedMark(mark: Mark) {
            marks.add(mark.createMarkView())
        }

        override fun removedMark(mark: Mark) {
            marks.remove(mark.point)
        }

    }
}
