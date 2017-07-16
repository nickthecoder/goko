package uk.co.nickthecoder.kogo.gui

import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.kogo.model.Board
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.GameListener

class ProblemView(override val mainWindow: MainWindow, val game: Game) : View, GameListener {

    override val title = "Problem ${game.file!!.nameWithoutExtension}"

    val board: Board
        get() = game.board

    protected val whole = BorderPane()

    protected val toolBar = ToolBar()

    protected val split = SplitPane()

    protected val boardView = BoardView(board)

    protected val commentsView = CommentsView(mainWindow, game)

    protected val passB = Button("Pass")

    protected val restartB = Button("Restart")

    override val node = whole

    override fun build(): View {
        whole.top = toolBar
        whole.center = split

        with(split) {
            items.add(boardView.node)
            items.add(commentsView.node)
            split.dividers[0].position = 0.7
        }
        commentsView.build()

        passB.addEventHandler(ActionEvent.ACTION) { onPass() }
        restartB.addEventHandler(ActionEvent.ACTION) { onRestart() }

        toolBar.items.addAll(passB, restartB)


        return this
    }

    fun onPass() {
        game.pass()
    }

    fun onRestart() {
        game.rewindTo(game.root)
    }

    override fun moved() {
    }

    override fun tidyUp() {
        game.tidyUp()
    }
}
