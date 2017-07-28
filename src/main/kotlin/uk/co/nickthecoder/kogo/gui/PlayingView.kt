package uk.co.nickthecoder.kogo.gui

import javafx.scene.control.SplitPane
import uk.co.nickthecoder.kogo.ScoreEstimator
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.paratask.gui.ShortcutHelper

open class PlayingView(mainWindow: MainWindow, game: Game) : AbstractGoView(mainWindow, game) {

    override val title = "Playing"

    protected val split = SplitPane()

    val shortcuts = ShortcutHelper("PlayingView", node)

    val gameInfoView = GameInfoView(game,true)

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

    override fun showScore(score: String) {
        gameInfoView.messageLabel.text = score
    }

}

