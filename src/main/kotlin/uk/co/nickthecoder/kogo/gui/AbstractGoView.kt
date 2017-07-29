package uk.co.nickthecoder.kogo.gui

import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.kogo.HintGenerator
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.ScoreEstimator
import uk.co.nickthecoder.kogo.model.*
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

    protected val history = History(game)

    val boardView = BoardView(game)

    private val shortcuts = ShortcutHelper("PlayingView", whole)

    protected val passB = KoGoActions.PASS.createButton(shortcuts) { onPass() }
    protected val saveB = KoGoActions.SAVE.createButton(shortcuts) { onSave() }
    protected val editB = KoGoActions.EDIT.createButton(shortcuts) { onEdit() }
    protected val resignB = KoGoActions.RESIGN.createButton(shortcuts) { onResign() }
    protected val hintB = KoGoActions.HINT.createButton(shortcuts) { HintGenerator(game).hint() }
    protected val estimateScoreB = KoGoActions.ESTIMATE_SCORE.createToggleButton { onCalculateScore() }

    protected val restartB = KoGoActions.GO_FIRST.createButton(shortcuts) { game.rewindTo(game.root) }
    protected val backB = KoGoActions.GO_BACK.createButton(shortcuts) { game.moveBack() }
    protected val rewindB = KoGoActions.GO_REWIND.createButton(shortcuts) { game.moveBack(10) }
    protected val forwardB = KoGoActions.GO_FORWARD.createButton(shortcuts) { history.forward() }
    protected val fastForwardB = KoGoActions.GO_FAST_FORWARD.createButton(shortcuts) { history.forward(10) }
    protected val endB = KoGoActions.GO_END.createButton(shortcuts) { onEnd() }

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
        val follower = FollowGame(game)
        val view = EditGameView(mainWindow, follower.copy)
        if (boardView.colorVariation != GameVariation.NORMAL) {
            // We probably want to see the current board position when playing one-color-go.
            view.onEnd()
        }
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

    fun onCalculateScore() {
        game.clearMarks()
        if (estimateScoreB.isSelected) {
            ScoreEstimator(game).estimate() {
                showScore(it)
            }
        }
    }

    open fun showScore(score: String) {}

    override fun moved() {
        val isLocal = game.playerToMove is LocalPlayer
        passB.isDisable = !isLocal
        resignB.isDisable = !isLocal
    }
}
