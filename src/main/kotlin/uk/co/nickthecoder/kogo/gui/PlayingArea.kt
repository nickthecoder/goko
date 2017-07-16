package uk.co.nickthecoder.kogo.gui

import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.Player
import uk.co.nickthecoder.kogo.model.*

class PlayingArea(val boardView: BoardView) : BoardListener {

    private val stack = StackPane()

    val node: Node
        get() = stack

    val stones = MarksViewArray(board = boardView.board)

    val marks = MarksView(board = boardView.board)

    val mouseMark = SymbolMark(Point(-10, -10), "mouse")

    val latestMark = SymbolMark(Point(-10, -10), "latest") // Initially off-screen

    val board: Board
        get() = boardView.board

    val game: Game
        get() = boardView.board.game

    init {
        with(stack) {
            styleClass.add("playing-area")
            children.addAll(stones.node, marks.node)
            prefWidth = boardView.board.sizeX * BoardView.pointSize
            prefHeight = boardView.board.sizeY * BoardView.pointSize
        }

        marks.add(mouseMark)
        marks.add(latestMark)

        stack.addEventHandler(MouseEvent.MOUSE_MOVED) { onMouseMoved(it) }
        stack.addEventHandler(MouseEvent.MOUSE_CLICKED) { onMouseClicked(it) }

        board.listeners.add(this)
    }

    fun onMouseMoved(event: MouseEvent) {
        val point = boardView.toBoardPoint(event.x, event.y)
        if (board.contains(point)) {
            mouseMark.point = point
        }
    }

    fun onMouseClicked(event: MouseEvent) {
        val point = boardView.toBoardPoint(event.x, event.y)
        val player = game.playerToMove

        if (player is LocalPlayer && game.canPlayAt(point)) {
            game.move(point, game.playerToMove.color, game.playerToMove)
        }
    }

    override fun stoneChanged(point: Point, byPlayer: Player?) {
        val color = board.getStoneAt(point)
        if (color == StoneColor.NONE) {
            stones.removeAt(point)
        } else {
            stones.add(SymbolMark(point, "stone" + if (color == StoneColor.WHITE) "W" else "B"))
            latestMark.point = point
            latestMark.color(if (color == StoneColor.BLACK) StoneColor.WHITE else StoneColor.BLACK)
        }
    }

    override fun moved() {
        mouseMark.color(game.playerToMove.color)
    }
}
