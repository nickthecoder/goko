package uk.co.nickthecoder.kogo.gui

import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.kogo.Player
import uk.co.nickthecoder.kogo.model.*

class StonesView(val boardView: BoardView) : GameListener {

    private val stack = StackPane()

    val node: Node
        get() = stack

    val stones = MarksViewArray(board = boardView.board)

    val specialMarks = MarksView(board = boardView.board)

    val marks = MarksView(board = boardView.board)

    val mouseMark = SymbolMarkView(Point(-10, -10), "mouse")

    val latestMark = SymbolMarkView(Point(-10, -10), "latest") // Initially off-screen

    val board: Board
        get() = boardView.board

    val game: Game
        get() = boardView.board.game

    private val OFF_SCREEN = Point(-100, -100)

    init {
        with(stack) {
            styleClass.add("playing-area")
            children.addAll(stones.node, marks.node, specialMarks.node)
            prefWidth = boardView.board.sizeX * BoardView.pointSize
            prefHeight = boardView.board.sizeY * BoardView.pointSize
        }

        specialMarks.add(mouseMark)
        specialMarks.add(latestMark)

        stack.addEventHandler(MouseEvent.MOUSE_MOVED) { onMouseMoved(it) }
        stack.addEventHandler(MouseEvent.MOUSE_CLICKED) { onMouseClicked(it) }
        stack.addEventHandler(MouseEvent.MOUSE_EXITED) { onMouseExited() }

        game.gameListeners.add(this)
    }

    fun onMouseMoved(event: MouseEvent) {
        val point = boardView.toBoardPoint(event.x, event.y)
        if (board.contains(point)) {
            mouseMark.point = point
        } else {
            mouseMark.point = OFF_SCREEN
        }
    }

    fun onMouseExited() {
        mouseMark.point = OFF_SCREEN
    }

    fun onMouseClicked(event: MouseEvent) {
        val point = boardView.toBoardPoint(event.x, event.y)
        val player = game.playerToMove

        if (player.canClickToPlay() && game.canPlayAt(point)) {
            game.move(point, game.playerToMove.color, game.playerToMove)
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

        val currentNode = game.currentNode
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
