package uk.co.nickthecoder.goko.shell

import javafx.scene.control.Button
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.model.ProblemSet
import uk.co.nickthecoder.goko.model.Problems

class ProblemsView(mainWindow: MainWindow) : GridView(mainWindow, 130.0) {

    override val title = "Problems"

    override val viewStyle = "problems"

    override fun addButtons() {
        Problems.problemSets().forEach {
            buttons.add(createButton(it))
        }
    }

    fun createButton(problemSet: ProblemSet): Button {
        val button = createButton(problemSet.label, style = null) { ProblemSetView(mainWindow, problemSet) }
        return button
    }

}
