package uk.co.nickthecoder.kogo.gui

import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.kogo.HintGenerator
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.model.Board
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.GameListener
import uk.co.nickthecoder.kogo.model.History
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.project.TaskPrompter

/**
 * Base class for views which have a toolbar
 */
abstract class AbstractGoView(mainWindow: MainWindow, val game: Game) : TopLevelView(mainWindow), GameListener {

    val board: Board
        get() = game.board

    protected val whole = BorderPane()

    override val node = whole

    protected val toolBar = ToolBar()

    val history = History(game)

    private val shortcuts = ShortcutHelper("PlayingView", node)

    protected val passB = KoGoActions.PASS.createButton(shortcuts) { onPass() }
    protected val saveB = KoGoActions.SAVE.createButton(shortcuts) { onSave() }
    protected val editB = KoGoActions.EDIT.createButton(shortcuts) { onEdit() }
    protected val resignB = KoGoActions.RESIGN.createButton(shortcuts) { onResign() }
    protected val hintB = KoGoActions.HINT.createButton(shortcuts) { HintGenerator(game).hint() }

    val restartB = KoGoActions.GO_FIRST.createButton(shortcuts) { game.rewindTo(game.root) }
    val backB = KoGoActions.GO_BACK.createButton(shortcuts) { game.moveBack() }
    val rewindB = KoGoActions.GO_REWIND.createButton(shortcuts) { game.moveBack(10) }
    val forwardB = KoGoActions.GO_FORWARD.createButton(shortcuts) { history.forward() }
    val fastForwardB = KoGoActions.GO_FAST_FORWARD.createButton(shortcuts) { history.forward(10) }
    val endB = KoGoActions.GO_END.createButton(shortcuts) { onEnd() }

    override fun build() {
        whole.top = toolBar
        game.listeners.add(this)
    }

    override fun tidyUp() {
        super.tidyUp()
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

    open fun onEdit() {
        val copy = game.copy()
        val view = EditGameView(mainWindow, copy)
        mainWindow.addViewAfter(this, view)
    }

    fun onSave() {
        val saveT = SaveGameTask(game)
        TaskPrompter(saveT).placeOnStage(Stage())
    }

    fun onEnd() {
        while (game.currentNode.children.isNotEmpty()) {
            game.currentNode.children[0].apply(game)
        }
    }

    override fun moved() {
        val isLocal = game.playerToMove is LocalPlayer
        passB.isDisable = !isLocal
        resignB.isDisable = !isLocal
    }
}
