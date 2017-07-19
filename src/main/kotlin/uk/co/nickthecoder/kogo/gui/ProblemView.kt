package uk.co.nickthecoder.kogo.gui

import javafx.event.ActionEvent
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import uk.co.nickthecoder.kogo.KoGo
import uk.co.nickthecoder.kogo.ProblemPlayer
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.kogo.preferences.Preferences

class ProblemView(mainWindow: MainWindow, val problem: Problem) : TopLevelView(mainWindow), GameListener {

    val game = problem.load()

    override val title = "Problem ${problem.label}"

    val board: Board
        get() = game.board

    private val whole = BorderPane()

    private val toolBar = ToolBar()

    private val split = SplitPane()

    private val rightPane = BorderPane()

    private val boardView = BoardView(board)

    private val commentsView = CommentsView(game)

    private val problemResults = ProblemResults()


    override val node = whole

    val firstPlayer = game.playerToMove

    val secondPlayer = ProblemPlayer(game, firstPlayer.color.opposite(), this)

    init {
        game.addPlayer(secondPlayer)
        game.gameListeners.add(this)
    }

    override fun build(): View {
        boardView.build()

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

        val passB = Button("Pass")
        passB.addEventHandler(ActionEvent.ACTION) { onPass() }

        val restartB = Button("Restart")
        restartB.addEventHandler(ActionEvent.ACTION) { onRestart() }

        toolBar.items.addAll(passB, restartB)

        game.root.apply(game)

        return this
    }

    fun onPass() {
        game.pass()
    }

    fun onRestart() {
        showProblem(problem)
    }

    fun showProblem(problem: Problem) {
        val view = ProblemView(mainWindow, problem)
        mainWindow.changeView(view)
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
        boardView.tidyUp()
    }

    inner class ProblemResults : VBox() {

        val group = ToggleGroup()

        fun build() {
            val failB = createRadioButton("Failed", ProblemResult.FAILED)
            val uncertainB = createRadioButton("Hmm, not sure!", ProblemResult.UNCERTAIN)
            val winB = createRadioButton("Solved", ProblemResult.SOLVED)

            val box = HBox()
            box.styleClass.add("problem-result")
            box.children.addAll(failB, uncertainB, winB)

            styleClass.add("problem-results")
            children.addAll(Label("Rate yourself"), box)


            val nextProblem = problem.next()
            if (nextProblem != null) {
                val nextProblemB = Button()
                nextProblemB.graphic = ImageView(KoGo.imageResource("go-next.png"))
                nextProblemB.tooltip = Tooltip("Next Problem")
                nextProblemB.addEventHandler(ActionEvent.ACTION) { showProblem(nextProblem) }
                box.children.add(nextProblemB)
            }
        }


        fun createRadioButton(tooltipStr: String, result: ProblemResult): ToggleButton {
            val button = ToggleButton()
            with(button) {
                graphic = ImageView(result.image())
                addEventHandler(ActionEvent.ACTION) { problem.saveResult(result) }
                toggleGroup = group
                tooltip = Tooltip(tooltipStr)
                if (problem.getResult() == result) {
                    button.setSelected(true)
                }
            }

            return button
        }
    }
}
