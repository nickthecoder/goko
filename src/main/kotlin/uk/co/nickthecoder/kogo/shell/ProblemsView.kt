package uk.co.nickthecoder.kogo.shell

import javafx.scene.control.Button
import uk.co.nickthecoder.kogo.gui.EditView
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.gui.ProblemView
import uk.co.nickthecoder.kogo.gui.View
import uk.co.nickthecoder.kogo.model.SGFReader
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File

class ProblemsView(mainWindow: MainWindow, val directory: File) : GridView(mainWindow) {

    override val title = directory.name

    override val buttonWidth = 16.0

    override val buttonHeight = 16.0

    override val viewStyle = "problems"

    override fun addButtons() {
        val lister = FileLister(extensions = listOf("sgf"))
        val files = lister.listFiles(directory)

        files.forEach { file ->
            buttons.add(createButton(file))
        }
    }

    fun createButton(file: File): Button {
        return createButton(file.nameWithoutExtension, style = null) { createView(file) }
    }

    fun createView(file: File): View {
        val reader = SGFReader(file)
        val game = reader.read()

        val view = ProblemView(mainWindow, game)
        game.rewindTo(game.root)
        game.root.apply(game)

        return view
    }
}
