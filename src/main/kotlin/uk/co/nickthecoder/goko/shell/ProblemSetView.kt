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
