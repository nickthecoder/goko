/*
GoKo a Go Client
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.goko.gui

import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.goko.HintGenerator
import uk.co.nickthecoder.goko.LocalPlayer
import uk.co.nickthecoder.goko.ScoreEstimator
import uk.co.nickthecoder.goko.model.*
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.project.TaskPrompter

/**
 * Base class for views which have a toolbar
 */
abstract class AbstractGoView(mainWindow: MainWindow, val game: Game) : TopLevelView(mainWindow), GameListener {

    val board: Board
        get() = game.board

    protected val whole = BorderPane()

    final override val node = whole

    protected val toolBar = ToolBar()

    protected val history = History(game)

    val boardView = BoardView(game)

    private val shortcuts = ShortcutHelper("PlayingView", whole)

    protected val passB = GoKoActions.PASS.createButton(shortcuts) { onPass() }
    protected val saveB = GoKoActions.SAVE.createButton(shortcuts) { onSave() }
    protected val editB = GoKoActions.EDIT.createButton(shortcuts) { onEdit() }
    protected val resignB = GoKoActions.RESIGN.createButton(shortcuts) { onResign() }
    protected val hintB = GoKoActions.HINT.createButton(shortcuts) { HintGenerator(game).hint() }
    protected val estimateScoreB = GoKoActions.ESTIMATE_SCORE.createToggleButton { onCalculateScore() }

    protected val restartB = GoKoActions.GO_FIRST.createButton(shortcuts) { game.rewindTo(game.root) }
    protected val backB = GoKoActions.GO_BACK.createButton(shortcuts) { game.moveBack() }
    protected val rewindB = GoKoActions.GO_REWIND.createButton(shortcuts) { game.moveBack(10) }
    protected val forwardB = GoKoActions.GO_FORWARD.createButton(shortcuts) { history.forward() }
    protected val fastForwardB = GoKoActions.GO_FAST_FORWARD.createButton(shortcuts) { history.forward(10) }
    protected val endB = GoKoActions.GO_END.createButton(shortcuts) { onEnd() }

    protected val undoB = GoKoActions.UNDO.createButton(shortcuts) { onUndo() }
    protected val checkGnuGoBoard = GoKoActions.CHECK_GNU_GO.createButton(shortcuts) { game.createGnuGo().checkBoard() }

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
            game.apply(game.currentNode.children[0])
        }
    }

    fun onCalculateScore() {
        game.clearMarks()
        if (estimateScoreB.isSelected) {
            ScoreEstimator(game).estimate {
                showScore(it)
            }
        }
    }

    fun onUndo() {
        game.moveBack()
        // Unless we are playing a 2-player local game, then undo MY previous move
        // (so far we have only undone my opponents move).
        if (!game.playerToMove.canClickToPlay()) {
            game.moveBack()
        }
    }

    open fun showScore(score: String) {}

    private fun update() {
        val isLocal = game.playerToMove is LocalPlayer
        passB.isDisable = !isLocal
        resignB.isDisable = !isLocal
    }

    override fun madeMove(gameNode: GameNode) {
        update()
    }

    override fun undoneMove(gameNode: GameNode) {
        update()
    }

}
