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

import javafx.event.ActionEvent
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import uk.co.nickthecoder.goko.ProblemOpponent
import uk.co.nickthecoder.goko.ProblemPlayer
import uk.co.nickthecoder.goko.model.*
import uk.co.nickthecoder.goko.preferences.Preferences
import uk.co.nickthecoder.goko.preferences.PreferencesView
import uk.co.nickthecoder.paratask.gui.ShortcutHelper

class ProblemView(mainWindow: MainWindow, val problem: Problem, val cheat: Boolean = false)
    : AbstractGoView(mainWindow, problem.load()) {

    override val title = "Problem ${problem.label}"

    private val split = SplitPane()

    private val rightPane = BorderPane()

    private val commentsView = CommentsView(game, true, Preferences.problemsPreferences)

    private val problemResults = ProblemResults()

    val firstPlayer = ProblemPlayer(game, game.playerToMove.color)

    val secondPlayer = ProblemOpponent(game, firstPlayer.color.opposite())

    val shortcuts = ShortcutHelper("ProblemView", node)

    init {
        game.addPlayer(firstPlayer)
        game.addPlayer(secondPlayer)
    }

    override fun build() {
        super.build()
        boardView.build()

        with(rightPane) {
            center = commentsView.node
            bottom = problemResults
        }

        whole.center = split

        with(split) {
            items.add(boardView.node)
            items.add(rightPane)
            dividers[0].position = 0.7
        }
        commentsView.build()
        problemResults.build()

        val preferencesB = GoKoActions.PREFERENCES.createButton { mainWindow.addView(PreferencesView(mainWindow, Preferences.problemsPreferences)) }
        val restartB = GoKoActions.PROBLEM_RESTART.createButton { onRestart() }
        val giveUpB = GoKoActions.PROBLEM_GIVE_UP.createButton(shortcuts) { onGiveUp() }

        toolBar.items.addAll(preferencesB, restartB, giveUpB, reviewB, passB)

        // TODO Is this really needed?
        game.apply(game.root)
    }

    override fun tidyUp() {
        super.tidyUp()
        game.tidyUp()
        boardView.tidyUp()
        commentsView.tidyUp()
    }

    fun onRestart() {
        showProblem(problem)
    }

    fun onGiveUp() {
        problem.saveResult(ProblemResult.FAILED)
        showProblem(problem, cheat = true)
    }

    override fun onEdit() {
        // NOTE, Currently this will NOT allow you to save the edited game if the problem is part of a multi-game .sgf file.
        val copy = problem.load()
        val view = EditGameView(mainWindow, copy)
        mainWindow.addViewAfter(this, view)
    }

    private fun showProblem(problem: Problem, cheat: Boolean = false) {
        val view = ProblemView(mainWindow, problem, cheat)
        mainWindow.changeView(view)
    }

    fun update() {
        val currentNode = game.currentNode

        if (cheat) {
            currentNode.children.firstOrNull().let { node ->
                if (node is MoveNode) {
                    val mark = MainLineMark(node.point)
                    game.addMark(mark)
                }
            }
        } else if (Preferences.problemsShowBranches == true) {
            if (currentNode.children.size > 1) {
                currentNode.children.filterIsInstance<MoveNode>().forEach { child ->
                    val mark = AlternateMark(child.point)
                    game.addMark(mark)
                }
            }
        }
    }

    override fun madeMove(gameNode: GameNode) {
        super.madeMove(gameNode)
        update()
    }

    override fun undoneMove(gameNode: GameNode) {
        super.undoneMove(gameNode)
        update()
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
                val nextProblemB = GoKoActions.PROBLEM_NEXT.createButton(shortcuts) { showProblem(nextProblem) }
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
                    button.isSelected = true
                }
            }

            return button
        }
    }
}
