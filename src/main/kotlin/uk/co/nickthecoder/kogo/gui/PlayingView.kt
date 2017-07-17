package uk.co.nickthecoder.kogo.gui

import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.model.Board
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.GameListener
import uk.co.nickthecoder.kogo.model.Mark

open class PlayingView(mainWindow: MainWindow, val game: Game) : TopLevelView(mainWindow), GameListener {

    override val title = "Playing"

    val board: Board
        get() = game.board

    protected val whole = BorderPane()

    protected val toolBar = ToolBar()

    protected val split = SplitPane()

    protected val boardView = BoardView(board)

    protected val passB = Button("Pass")

    protected val resignB = Button("Resign")

    protected val undoB = Button("Undo")

    override val node = whole

    override fun build(): View {
        boardView.build()
        whole.top = toolBar
        whole.center = split

        split.items.add(boardView.node) // TODO Add a status are on the right

        passB.addEventHandler(ActionEvent.ACTION) { onPass() }
        resignB.addEventHandler(ActionEvent.ACTION) { onResign() }
        undoB.addEventHandler(ActionEvent.ACTION) { onUndo() }

        toolBar.items.addAll(passB, resignB)

        return this
    }

    fun onPass() {
        if (game.playerToMove is LocalPlayer) {
            game.pass()
        }
    }

    fun onResign() {
        if (game.playerToMove is LocalPlayer) {
            game.resign(game.playerToMove)
        }
    }

    fun onUndo() {
        game.undo()
    }

    override fun moved() {
        val isLocal = game.playerToMove is LocalPlayer
        passB.isDisable = !isLocal
        resignB.isDisable = !isLocal
    }

    override fun tidyUp() {
        game.tidyUp()
        boardView.tidyUp()
    }
}
