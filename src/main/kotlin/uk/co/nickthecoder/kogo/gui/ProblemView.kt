package uk.co.nickthecoder.kogo.gui

import javafx.event.ActionEvent
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import uk.co.nickthecoder.kogo.ProblemOpponent
import uk.co.nickthecoder.kogo.ProblemPlayer
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.kogo.preferences.PreferencesView
import uk.co.nickthecoder.paratask.gui.ShortcutHelper

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

    private val commentsView = CommentsView(game, true, Preferences.problemsPreferences)

    private val problemResults = ProblemResults()


    override val node = whole

    val firstPlayer = ProblemPlayer(game, game.playerToMove.color)

    val secondPlayer = ProblemOpponent(game, firstPlayer.color.opposite(), this)

    val shortcuts = ShortcutHelper("ProblemView", node)

    init {
        game.addPlayer(firstPlayer)
        game.addPlayer(secondPlayer)
        game.listeners.add(this)
    }

    override fun build() {
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
            dividers[0].position = 0.7
        }
        commentsView.build()
        problemResults.build()

        val preferencesB = KoGoActions.PREFERENCES.createButton { mainWindow.addView(PreferencesView(mainWindow, Preferences.problemsPreferences)) }

        val passB = KoGoActions.PASS.createButton(shortcuts) { onPass() }
        val restartB = KoGoActions.PROBLEM_RESTART.createButton(shortcuts) { onRestart() }
        val giveUpB = KoGoActions.PROBLEM_GIVE_UP.createButton(shortcuts) { onGiveUp() }
        val editB = KoGoActions.EDIT.createButton(shortcuts) { onEdit() }

        toolBar.items.addAll(preferencesB, restartB, giveUpB, editB, passB)

        game.root.apply(game)
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

    fun onEdit() {
        // NOTE, Currently this will NOT allow you to save the edited game if the problem is part of a multi-game .sgf file.
        val copy = problem.load()
        val view = EditGameView(mainWindow, copy)
        mainWindow.addViewAfter(this, view)
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
                    val mark = MainLineMark(node.point)
                    game.addMark(mark)
                }
            }
        } else if (Preferences.problemsShowContinuations == true) {
            if (currentNode.children.size > 1) {
                for (child in currentNode.children) {
                    if (child is MoveNode) {
                        val mark = AlternateMark(child.point)
                        game.addMark(mark)
                    }
                }
            }
        }
    }

    override fun tidyUp() {
        game.tidyUp()
        boardView.tidyUp()
        commentsView.tidyUp()
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
                val nextProblemB = KoGoActions.PROBLEM_NEXT.createButton(shortcuts) { showProblem(nextProblem) }
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
