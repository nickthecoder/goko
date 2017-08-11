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
package uk.co.nickthecoder.goko.shell

import javafx.scene.control.Button
import javafx.scene.layout.HBox
import uk.co.nickthecoder.goko.gui.GoKoActions
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.gui.ProblemView
import uk.co.nickthecoder.goko.gui.TopLevelView
import uk.co.nickthecoder.goko.model.Problem
import uk.co.nickthecoder.goko.model.ProblemResult
import uk.co.nickthecoder.goko.model.ProblemSet
import uk.co.nickthecoder.goko.model.ProblemSetListener

class ProblemSetView(mainWindow: MainWindow, val problemSet: ProblemSet) : GridView(mainWindow, 48.0), ProblemSetListener {

    override val title = problemSet.label

    override val viewStyle = "problem-set"

    val resetB = GoKoActions.PROBLEMS_RESET.createButton() { onReset() }

    val buttonBar = HBox()

    override fun build() {
        problemSet.listeners.add(this)
        super.build()
        whole.bottom = buttonBar
        buttonBar.styleClass.add("buttons")
        buttonBar.children.add(resetB)
    }

    override fun createButtons() {
        super.createButtons()
        var i = 1
        problemSet.problems.values.forEach { problem ->
            buttons.add(createProblemButton(i.toString(), problem))
            i++
        }
    }

    fun createProblemButton(label: String, problem: Problem): Button {
        return createViewButton(label, style = problem.result.style()) { createView(problem) }
    }

    fun createView(problem: Problem): TopLevelView = ProblemView(mainWindow, problem)

    override fun updated() {
        createButtons()
        buildButtons()
    }

    fun onReset() {
        problemSet.problems.values.forEach { problem ->
            problem.result = ProblemResult.UNTRIED
        }
        problemSet.saveResults()
        buildButtons()
    }
}
