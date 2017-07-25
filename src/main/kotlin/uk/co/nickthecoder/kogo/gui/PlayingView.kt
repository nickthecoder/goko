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

    override val node = whole

    val gameInfoView = GameInfoView(game)

    override fun build() {
        boardView.build()
        gameInfoView.build()

        whole.top = toolBar
        whole.center = split

        with(split) {
            items.addAll(boardView.node, gameInfoView.node)
            dividers[0].position = 0.7
        }

        passB.addEventHandler(ActionEvent.ACTION) { onPass() }
        resignB.addEventHandler(ActionEvent.ACTION) { onResign() }
        editB.addEventHandler(ActionEvent.ACTION) { onEditGame() }

        toolBar.items.addAll(passB, resignB, editB)
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
        gameInfoView.tidyUp()
    }

}

