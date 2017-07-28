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
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.paratask.util.Labelled


class BoardView(val game: Game) : View {

    val board = game.board

    private val container = StackPane()

    var scale: Double = 1.0

    var oldScale: Double = 0.0

    val stones = StonesView(this)

    var clickBoardView: ClickBoardView = ClickBoardView(this@BoardView)

    val specialMarks = MarksView(board)

    val marks = MarksView(board)

    val moveNumbers = MarksView(board)

    val continuations = MarksView(board)

    private var mouseMode = MouseMode.PLAYING

    private var mouseMark = SymbolMarkView(Point(-10, -10), "place-stone")

    private val latestMark = SymbolMarkView(Point(-10, -10), "latest") // Initially off-screen

    var showBranches = ShowBranches.DO_NOT_SHOW
        set(v) {
            field = v
            updateContinuations()
        }

    var showMoveNumbers: Int = 0
        set(v) {
            field = v
            updateMoveNumbers()
        }

    var colorVariation: ColorVariation = ColorVariation.NORMAL
        set(v) {
            field = v
            stones.requestLayout()
        }

    override val node: Parent
        get() = container

    private val boardLayout = BoardLayout()

    val OFF_SCREEN = Point(-100, -100)


    override fun build() {
        with(container) {
            styleClass.add("board-container")
            children.add(boardLayout)
        }
        boardLayout.build()
        playing()
    }

    fun playing() {
        mouseMark.style("place-stone")
        mouseMark.colorWhite(game.playerToMove.color == StoneColor.WHITE)
        mouseMode = MouseMode.PLAYING
    }

    fun placingStone(color: StoneColor) {
        mouseMark.style("place-stone")
        mouseMark.colorWhite(color == StoneColor.WHITE)
        mouseMode = MouseMode.ADDING_STONES
    }

    fun placingMark() {
        mouseMark.style("place-mark")
        mouseMode = MouseMode.MARKING
    }

    fun removingMark() {
        mouseMark.style("remove-stone")
        mouseMode = MouseMode.REMOVING_MARKS
    }

    fun removingStone() {
        mouseMark.style("remove-mark")
        mouseMode = MouseMode.REMOVING_STONES
    }

    fun mouseMarkAt(point: Point?) {
        if (point == null) {
            mouseMark.point = OFF_SCREEN
        } else {
            var show = true

            if (mouseMode == MouseMode.PLAYING || mouseMode == MouseMode.ADDING_STONES) {
                if (board.getStoneAt(point).isStone()) {
                    show = false
                }
            } else if (mouseMode == MouseMode.REMOVING_STONES) {
                if (!board.getStoneAt(point).isStone()) {
                    show = false
                }
            } else if (mouseMode == MouseMode.REMOVING_MARKS) {
                if (!game.currentNode.hasMarkAt(point)) {
                    show = false
                }
            }

            if (show) {
                mouseMark.point = point
            } else {
                mouseMark.point = OFF_SCREEN
            }

            if (mouseMode == MouseMode.MARKING || mouseMode == MouseMode.REMOVING_MARKS || mouseMode == MouseMode.REMOVING_STONES) {
                mouseMark.colorWhite(board.getStoneAt(point) == StoneColor.BLACK)
            }
        }
    }

    fun toBoardPoint(x: Double, y: Double): Point {
        return Point((x / pointSize).toInt(), board.size - (y / pointSize).toInt() - 1)
    }

    fun updateContinuations() {
        continuations.clear()
        val currentNode = game.currentNode
        if (showBranches != ShowBranches.DO_NOT_SHOW) {
            var index = 1
            var markView: MarkView
            currentNode.children.filter { it is MoveNode && !currentNode.hasMarkAt(it.point) }.map { it as MoveNode }.forEach { child ->
                val mark: SymbolMark
                if (showBranches == ShowBranches.NUMBERS) {
                    markView = MarkView(LabelMark(child.point, index.toString()))
                } else {
                    if (index == 1) {
                        mark = MainLineMark(child.point)
                    } else {
                        mark = AlternateMark(child.point)
                    }
                    markView = SymbolMarkView(mark)
                }
                continuations.add(markView)
                index++
            }
        }
    }

    fun updateMoveNumbers() {
        moveNumbers.clear()
        val currentNode = game.currentNode
        latestMark.point = OFF_SCREEN

        if (showMoveNumbers == 0) {
            if (currentNode is MoveNode) {
                if (!currentNode.hasMarkAt(currentNode.point)) {
                    latestMark.point = currentNode.point
                }
            }
        }

        var node = currentNode
        for (i in 1..showMoveNumbers) {
            if (node is MoveNode) {
                if (!currentNode.hasMarkAt(node.point)) {
                    val mv = MarkView(LabelMark(node.point, node.moveNumber.toString()))
                    moveNumbers.add(mv)
                }
            }
            if (node.parent == null) {
                break
            } else {
                node = node.parent!!
            }
        }
    }

    companion object {
        val pointSize = 128.0
    }

    inner class BoardLayout : Pane(), GameListener {

        val lines = Path()

        val starPoints = Pane()

        private val boardMarks = MarksView(board) // Star points and labels around the board

        private val wood = Region()

        init {
            board.game.listeners.add(this)

            specialMarks.add(mouseMark)
            specialMarks.add(latestMark)
        }

        fun build() {
            children.addAll(wood, lines, starPoints, boardMarks.node, stones, moveNumbers.node, continuations.node, marks.node, specialMarks.node, clickBoardView.node)

            with(wood) {
                styleClass.add("wood")
            }

            with(boardMarks) {
                styleClass.add("board-marks")
                for (x in 0..board.size - 1) {
                    val text = Point.labelX(x)
                    val bottomLabel = MarkView(Point(x, -1), "coordinate", text)
                    val topLabel = MarkView(Point(x, board.size), "coordinate", text)
                    add(topLabel)
                    add(bottomLabel)
                }

                for (y in 0..board.size - 1) {
                    val text = Point.labelY(y)
                    val leftLabel = MarkView(Point(-1, y), "coordinate", text)
                    val rightLabel = MarkView(Point(board.size, y), "coordinate", text)
                    add(leftLabel)
                    add(rightLabel)
                }
            }
        }

        override fun layoutChildren() {

            val marginTop: Double
            val marginLeft: Double
            var size: Double

            if (width > height) {
                size = height
                marginLeft = (width - height) / 2
                marginTop = 0.0
            } else {
                size = width
                marginLeft = 0.0
                marginTop = (height - width) / 2
            }
            // By flooring the scale, the lines of the grid will always be aligned to pixels on the screen, and therefore
            // won't need antialiasing, which can give inellegant results. The only down side, is that the board scales in
            // jumps when the view is resized by dragging.
            scale = Math.floor(size / (board.size + 2))
            size = scale * (board.size + 2)

            if (scale == oldScale) {
                return
            }
            oldScale = scale

            val playingSize = scale * board.size
            val logicalSize = (board.size + 2) * pointSize

            layoutInArea(wood, marginLeft, marginTop, size, size, 0.0, HPos.LEFT, VPos.TOP)
            layoutInArea(boardMarks.node, marginLeft + scale, marginTop + scale, logicalSize, logicalSize, 0.0, HPos.LEFT, VPos.TOP)
            layoutInArea(starPoints, marginLeft + scale * 1.5, marginTop + scale * 1.5, playingSize, playingSize, 0.0, HPos.LEFT, VPos.TOP)
            layoutInArea(moveNumbers.node, marginLeft + scale, marginTop + scale, logicalSize, logicalSize, 0.0, HPos.LEFT, VPos.TOP)
            layoutInArea(continuations.node, marginLeft + scale, marginTop + scale, logicalSize, logicalSize, 0.0, HPos.LEFT, VPos.TOP)
            layoutInArea(marks.node, marginLeft + scale, marginTop + scale, logicalSize, logicalSize, 0.0, HPos.LEFT, VPos.TOP)
            layoutInArea(specialMarks.node, marginLeft + scale, marginTop + scale, logicalSize, logicalSize, 0.0, HPos.LEFT, VPos.TOP)
            layoutInArea(clickBoardView.node, marginLeft + scale, marginTop + scale, logicalSize, logicalSize, 0.0, HPos.LEFT, VPos.TOP)

            layoutInArea(stones, marginLeft + scale, marginTop + scale, size, size, 0.0, HPos.LEFT, VPos.TOP)

            with(boardMarks.node) {
                translateX = (size - logicalSize) / 2
                translateY = translateX
                scaleX = scale / pointSize
                scaleY = scaleX
            }

            with(moveNumbers.node) {
                translateX = (size - logicalSize) / 2
                translateY = translateX
                scaleX = scale / pointSize
                scaleY = scaleX
            }

            with(continuations.node) {
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

            with(clickBoardView.node) {
                translateX = (size - logicalSize) / 2
                translateY = translateX
                scaleX = scale / pointSize
                scaleY = scaleX
            }

            lines.strokeWidth = scale / 70.0

            lines.elements.clear()
            for (x in 0..board.size - 1) {
                val from = MoveTo((x + 0.5) * scale - 1, 0.5 * scale - 1)
                val to = LineTo((x + 0.5) * scale - 1, (board.size - 0.5) * scale - 1)
                lines.elements.addAll(from, to)
            }
            for (y in 0..board.size - 1) {
                val from = MoveTo(0.5 * scale - 1, (y + 0.5) * scale - 1)
                val to = LineTo((board.size - 0.5) * scale - 1, (y + 0.5) * scale - 1)
                lines.elements.addAll(from, to)
            }

            if (board.size == 19) {
                starPoints(3, 3, 6)
            } else if (board.size == 13) {
                starPoints(3, 3, 6)
            } else if (board.size == 9) {
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

        override fun moved() {
            marks.clear()
            for (mark in board.game.currentNode.marks) {
                marks.add(mark.createMarkView())
            }
            updateMoveNumbers()
            updateContinuations()
            if (mouseMode == MouseMode.PLAYING) {
                mouseMark.colorWhite(game.playerToMove.color == StoneColor.WHITE)
            }
        }

        override fun addedMark(mark: Mark) {
            marks.add(mark.createMarkView())
            updateContinuations()
            updateMoveNumbers()
        }

        override fun removedMark(mark: Mark) {
            marks.remove(mark.point)
        }

    }
}

enum class MouseMode {
    PLAYING, ADDING_STONES, MARKING, REMOVING_STONES, REMOVING_MARKS
}

enum class ShowBranches(override val label: String) : Labelled {
    DO_NOT_SHOW("Do not show"),
    NUMBERS("As Numbers"),
    SYMBOLS("As Symbols")
}

enum class ColorVariation(override val label: String) : Labelled {
    NORMAL("Normal"),
    ONE_COLOR_GO("One Color Go"),
    TWO_COLOR_ONE_COLOR_GO("Two Color One Color Go")
}
