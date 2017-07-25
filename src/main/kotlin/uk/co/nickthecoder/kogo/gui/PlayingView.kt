package uk.co.nickthecoder.kogo.gui

import javafx.scene.control.SplitPane
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.model.Board
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.GameListener
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.project.TaskPrompter

open class PlayingView(mainWindow: MainWindow, val game: Game) : TopLevelView(mainWindow), GameListener {

    override val title = "Playing"

    val board: Board
        get() = game.board

    protected val whole = BorderPane()

    protected val toolBar = ToolBar()

    protected val split = SplitPane()

    protected val boardView = BoardView(game)

    override val node = whole

    val gameInfoView = GameInfoView(game)

    val shortcuts = ShortcutHelper("PlayingView", node)

    val passB = KoGoActions.PASS.createButton(shortcuts) { onPass() }

    val resignB = KoGoActions.RESIGN.createButton(shortcuts) { onResign() }

    override fun build() {
        boardView.build()
        gameInfoView.build()

        whole.top = toolBar
        whole.center = split

        with(split) {
            items.addAll(boardView.node, gameInfoView.node)
            dividers[0].position = 0.7
        }

        val editB = KoGoActions.EDIT.createButton(shortcuts) { onEdit() }
        val saveB = KoGoActions.SAVE.createButton(shortcuts) { onSave() }
        toolBar.items.addAll(saveB, editB, resignB, passB)
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

    fun onEdit() {
        val copy = game.copy()
        val view = EditGameView(mainWindow, copy)
        mainWindow.addViewAfter(this, view)
    }

    fun onSave() {
        val saveT = SaveGameTask(game)
        TaskPrompter(saveT).placeOnStage(Stage())
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

