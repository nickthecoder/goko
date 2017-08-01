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
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.gui.ProblemView
import uk.co.nickthecoder.goko.gui.TopLevelView
import uk.co.nickthecoder.goko.model.Problem
import uk.co.nickthecoder.goko.model.ProblemSet
import uk.co.nickthecoder.goko.model.ProblemSetListener

class ProblemSetView(mainWindow: MainWindow, val problemSet: ProblemSet) : GridView(mainWindow, 48.0), ProblemSetListener {

    override val title = problemSet.label

    override val viewStyle = "problem-set"

    init {
        problemSet.listeners.add(this)
    }

    override fun addButtons() {
        buttons.clear()
        var i = 1
        problemSet.problems.forEach { problem ->
            buttons.add(createButton(i.toString(), problem))
            i++
        }
    }

    fun createButton(label: String, problem: Problem): Button {
        return createButton(label, style = problem.getResult().style()) { createView(problem) }
    }

    fun createView(problem: Problem): TopLevelView = ProblemView(mainWindow, problem)

    override fun updated() {
        addButtons()
        buildButtons()
    }
}
