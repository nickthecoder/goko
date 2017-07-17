package uk.co.nickthecoder.kogo.gui

import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.ProblemPlayer
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.kogo.preferences.Preferences

class ProblemView(mainWindow: MainWindow, val game: Game) : TopLevelView(mainWindow), GameListener {

    override val title = "Problem ${game.file!!.nameWithoutExtension}"

    val board: Board
        get() = game.board

    private val whole = BorderPane()

    private val toolBar = ToolBar()

    private val split = SplitPane()

    private val rightPane = BorderPane()

    private val boardView = BoardView(board)

    private val commentsView = CommentsView(game)

    private val problemResults = ProblemResults()

    private val passB = Button("Pass")

    private val restartB = Button("Restart")

    override val node = whole

    val firstPlayer = game.playerToMove

    val secondPlayer = ProblemPlayer(game, firstPlayer.color.opposite(), this)

    init {
        game.addPlayer(secondPlayer)
        game.gameListeners.add(this)
    }

    override fun build(): View {
        with(rightPane) {
            center = commentsView.node
            bottom = problemResults
        }

        whole.top = toolBar
        whole.center = split

        with(split) {
            items.add(boardView.node)
            items.add(rightPane)
            split.dividers[0].position = 0.7
        }
        commentsView.build()
        problemResults.build()

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
        if (Preferences.problemsShowContinuations == true) {
            val currentNode = game.currentNode
            if (currentNode.children.size > 1) {
                for (child in currentNode.children) {
                    if (child is MoveNode) {
                        val mark = SquareMark(child.point)
                        game.addMark(mark)
                    }
                }
            }
        }
    }

    override fun tidyUp() {
        game.tidyUp()
    }


    class ProblemResults : VBox() {
        // TODO Add buttons to allow self-appraisal.

        fun build() {
            styleClass.add("problem-results")
            children.add(Label("Results"))
        }
    }

}
