package uk.co.nickthecoder.kogo.shell

import javafx.scene.control.Button
import uk.co.nickthecoder.kogo.ProblemPlayer
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.gui.ProblemView
import uk.co.nickthecoder.kogo.gui.TopLevelView
import uk.co.nickthecoder.kogo.model.SGFReader
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File

class ProblemsView(mainWindow: MainWindow, val directory: File) : GridView(mainWindow) {

    override val title = directory.name

    override val buttonSize = 48.0

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

    fun createView(file: File): TopLevelView {
        val reader = SGFReader(file)
        val game = reader.read()

        val view = ProblemView(mainWindow, game)
        game.root.apply(game)

        val firstPlayer = game.playerToMove
        val secondPlayer = ProblemPlayer(game, firstPlayer.color.opposite())
        game.addPlayer(secondPlayer)

        return view
    }
}
