package uk.co.nickthecoder.kogo.shell

import javafx.scene.control.Button
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.gui.ProblemView
import uk.co.nickthecoder.kogo.gui.TopLevelView
import uk.co.nickthecoder.kogo.model.SGFReader
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.File

class ProblemsView(mainWindow: MainWindow, val directory: File) : GridView(mainWindow) {

    override val title = directory.name

    override val buttonSize = 48

    Kogo is a cross-platform Go board.

    You can play Go against the computer (using GNU's go AI), play a local two player game, review and edit games and hone your go skills by solving go problems.

    It is written in Kotlin, which means it can run on any operating system supporting Java.
    Current Status
    edit

    Kogo is still in the early stages of development. However, you can already...

    Play against the computer (using GNU's Go AI)
    Play locally against another person

    Partially working features...

    Solve Go problems
    Load .sgf files
    Review games

    Future Goals
    edit

    Save games as .sgf files
    Talk to various go servers, playing on-line games with other people
    Play Go variants, such as "Hidden Move Go" and "One Colour Go"
    A version of Kogo for Android tablets (Kogo will never support Apple tablets)
========
    http://nickthecoder.co.uk/wiki/view/software/Kogohttp://nickthecoder.co.uk/wiki/view/software/Kogo .0
    https://github.com/nickthecoder/kogo.githttps://github.com/nickthecoder/kogo.git
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
        game.rewindTo(game.root)
        game.root.apply(game)

        return view
    }
}
