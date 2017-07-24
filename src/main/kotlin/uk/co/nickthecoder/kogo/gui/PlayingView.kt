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

open class PlayingView(mainWindow: MainWindow, val game: Game) : TopLevelView(mainWindow), GameListener {

    override val title = "Playing"

    val board: Board
        get() = game.board

    protected val whole = BorderPane()

    protected val toolBar = ToolBar()

    protected val split = SplitPane()

    protected val boardView = BoardView(game)

    protected val passB = Button("Pass")

    protected val resignB = Button("Resign")

    protected val editB = Button("Edit Game")

    protected val undoB = Button("Undo")

    override val node = whole

    override fun build(): View {
        boardView.build()
        whole.top = toolBar
        whole.center = split

        split.items.add(boardView.node) // TODO Add a status area on the right

        passB.addEventHandler(ActionEvent.ACTION) { onPass() }
        resignB.addEventHandler(ActionEvent.ACTION) { onResign() }
        undoB.addEventHandler(ActionEvent.ACTION) { onUndo() }
        editB.addEventHandler(ActionEvent.ACTION) { onEditGame() }

        toolBar.items.addAll(passB, resignB, editB)

        return this
    }

    fun onPass() {
        if (game.playerToMove is LocalPlayer) {
            game.playerToMove.pass()
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

    fun onEditGame() {
        val copy = game.copy()
        val view = EditGameView(mainWindow, copy)
        mainWindow.addViewAfter(this, view)
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
