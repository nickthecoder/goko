package uk.co.nickthecoder.kogo.gui

import javafx.scene.control.SplitPane
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.kogo.HintGenerator
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.ScoreEstimator
import uk.co.nickthecoder.kogo.model.Board
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.GameListener
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.project.TaskPrompter

open class PlayingView(mainWindow: MainWindow, game: Game) : AbstractGoView(mainWindow, game) {

    override val title = "Playing"

    protected val split = SplitPane()

    protected val boardView = BoardView(game)

    val shortcuts = ShortcutHelper("PlayingView", node)

    val estimateScoreB = KoGoActions.ESTIMATE_SCORE.createButton { onEstimateScore() }

    val gameInfoView = GameInfoView(game)

    override fun build() {
        super.build()
        boardView.build()
        gameInfoView.build()

        whole.center = split

        with(split) {
            items.addAll(boardView.node, gameInfoView.node)
            dividers[0].position = 0.7
        }

        toolBar.items.addAll(saveB, editB, hintB, estimateScoreB, resignB, passB)
    }

    override fun tidyUp() {
        super.tidyUp()
        game.tidyUp()
        boardView.tidyUp()
        gameInfoView.tidyUp()
    }

    fun onEstimateScore() {
        ScoreEstimator(game).estimate() {
            gameInfoView.messageLabel.text = it
        }
    }
}

