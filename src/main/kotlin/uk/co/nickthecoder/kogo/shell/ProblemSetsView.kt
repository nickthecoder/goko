package uk.co.nickthecoder.kogo.shell

import javafx.scene.control.Button
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File

class ProblemSetsView(mainWindow: MainWindow) : GridView(mainWindow) {

    override val title = "Problems"

    override val viewStyle = "problem-sets"

    override fun addButtons() {
        val lister = FileLister(onlyFiles = false)
        val directories = lister.listFiles(Preferences.problemsDirectory!!)
        directories.forEach { dir ->
            buttons.add(createButton(dir))
        }
    }

    fun createButton(directory: File): Button {
        val button = createButton(directory.name, style = null) { ProblemsView(mainWindow, directory) }
        return button
    }

}
