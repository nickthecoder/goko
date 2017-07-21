package uk.co.nickthecoder.kogo.gui

import javafx.event.ActionEvent
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import uk.co.nickthecoder.kogo.KoGo
import uk.co.nickthecoder.kogo.ProblemOpponent
import uk.co.nickthecoder.kogo.ProblemPlayer
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.kogo.preferences.Preferences

class ProblemView(mainWindow: MainWindow, val problem: Problem, val cheat: Boolean = false)
    : TopLevelView(mainWindow), GameListener {

    val game = problem.load()

    override val title = "Problem ${problem.label}"

    val board: Board
        get() = game.board

    private val whole = BorderPane()

    private val toolBar = ToolBar()

    private val split = SplitPane()

    private val rightPane = BorderPane()

    private val boardView = BoardView(game)

    private val commentsView = CommentsView(game)

    private val problemResults = ProblemResults()


    override val node = whole

    val firstPlayer = ProblemPlayer(game, game.playerToMove.color)

    val secondPlayer = ProblemOpponent(game, firstPlayer.color.opposite(), this)

    init {
        game.addPlayer(firstPlayer)
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
        passB.addEventHandler(ActionEvent.ACTION) { onPass() }

        val giveUpB = Button("Give Up")
        giveUpB.addEventHandler(ActionEvent.ACTION) { onGiveUp() }

        toolBar.items.addAll(passB, restartB, giveUpB)

        game.root.apply(game)

        return this
    }

    fun onPass() {
        firstPlayer.pass()
    }

    fun onRestart() {
        showProblem(problem)
    }

    fun onGiveUp() {
        problem.saveResult(ProblemResult.FAILED)
        showProblem(problem, cheat = true)
    }

    fun showProblem(problem: Problem, cheat: Boolean = false) {
        val view = ProblemView(mainWindow, problem, cheat)
        mainWindow.changeView(view)
    }

    override fun moved() {
        val currentNode = game.currentNode

        if (cheat) {
            currentNode.children.firstOrNull().let { node ->
                if (node is MoveNode) {
                    val mark = CircleMark(node.point)
                    game.addMark(mark)
                }
            }
        } else if (Preferences.problemsShowContinuations == true) {
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
                addEventHandler(ActionEvent.ACTION) {
                    if (button.isSelected) {
                        problem.saveResult(result)
                    } else {
                        problem.saveResult(ProblemResult.UNTRIED)
                    }
                }
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
