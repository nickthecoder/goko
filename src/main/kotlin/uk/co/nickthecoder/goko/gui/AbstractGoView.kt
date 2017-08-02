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

import javafx.scene.control.ToggleGroup
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.goko.*
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
    protected val reviewB = GoKoActions.REVIEW.createButton(shortcuts) { onEdit() }
    protected val resignB = GoKoActions.RESIGN.createButton(shortcuts) { onResign() }
    protected val hintB = GoKoActions.HINT.createButton(shortcuts) { HintGenerator(game).hint() }

    protected val estimateScoreB = GoKoActions.ESTIMATE_SCORE.createToggleButton(shortcuts) { showVisualisations() }
    protected val hotspotsB = GoKoActions.HOTSPOTS.createToggleButton(shortcuts) { showVisualisations() }
    protected val influenceB = GoKoActions.INFLUENCE.createToggleButton(shortcuts) { showVisualisations() }
    protected val visualiseGroup = ToggleGroup()

    init {
        visualiseGroup.toggles.addAll(influenceB, estimateScoreB, hotspotsB)
    }

    protected val restartB = GoKoActions.GO_FIRST.createButton(shortcuts) { game.rewindTo(game.root) }
    protected val backB = GoKoActions.GO_BACK.createButton(shortcuts) { game.moveBack() }
    protected val rewindB = GoKoActions.GO_REWIND.createButton(shortcuts) { game.moveBack(10) }
    protected val forwardB = GoKoActions.GO_FORWARD.createButton(shortcuts) { history.forward() }
    protected val fastForwardB = GoKoActions.GO_FAST_FORWARD.createButton(shortcuts) { history.forward(10) }
    protected val endB = GoKoActions.GO_END.createButton(shortcuts) { onEnd() }

    protected val undoB = GoKoActions.UNDO.createButton(shortcuts) { onUndo() }

    init {
        GoKoActions.CHECK_GNU_GO.createButton(shortcuts) { game.createGnuGo().checkBoard() }
    }

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
        if (game.variation.type != GameVariationType.NORMAL) {
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

    fun onUndo() {
        game.moveBack()
        // Unless we are playing a 2-player local game, then undo MY previous move
        // (so far we have only undone my opponents move).
        if (!game.playerToMove.canClickToPlay()) {
            game.moveBack()
        }
    }

    fun showVisualisations() {
        boardView.visualisation.clear()
        if (influenceB.isSelected) {
            VisualiseInfluence(game, StoneColor.BLACK, "black_influence", boardView.visualisation).visualise()
            VisualiseInfluence(game, StoneColor.WHITE, "white_influence", boardView.visualisation).visualise()
        }
        if (hotspotsB.isSelected) {
            VisualiseHotspots(game, boardView.visualisation).visualise()
        }
        if (estimateScoreB.isSelected) {
            ScoreEstimator(game).score { showScore(it) }
            VisualiseTerritory(game, StoneColor.WHITE, boardView.visualisation).visualise()
            VisualiseTerritory(game, StoneColor.BLACK, boardView.visualisation).visualise()
        }
    }

    open fun showScore(score: String) {}

    private fun update() {
        showVisualisations()
        val canClick = game.playerToMove.canClickToPlay()
        passB.isDisable = !canClick
        resignB.isDisable = !canClick
        boardView.visualisation.clear()
    }

    override fun madeMove(gameNode: GameNode) {
        update()
    }

    override fun undoneMove(gameNode: GameNode) {
        update()
    }

}
