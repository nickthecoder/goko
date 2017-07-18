package uk.co.nickthecoder.kogo.shell

import javafx.scene.control.Button
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.gui.ProblemView
import uk.co.nickthecoder.kogo.gui.TopLevelView
import uk.co.nickthecoder.kogo.model.Problem
import uk.co.nickthecoder.kogo.model.ProblemSet
import uk.co.nickthecoder.kogo.model.ProblemSetListener
import uk.co.nickthecoder.kogo.model.SGFReader
import java.io.File

class ProblemSetView(mainWindow: MainWindow, val problemSet: ProblemSet) : GridView(mainWindow), ProblemSetListener {

    override val title = problemSet.label

    override val buttonSize = 48.0

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
