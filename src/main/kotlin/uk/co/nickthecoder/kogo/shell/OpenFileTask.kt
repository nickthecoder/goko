package uk.co.nickthecoder.kogo.shell

import uk.co.nickthecoder.kogo.gui.EditGameView
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.model.SGFReader
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter

class OpenFileTask(val mainWindow: MainWindow) : AbstractTask() {

    override val taskD = TaskDescription("openFile", "Open an SGF File")

    val fileP = FileParameter("file", value = Preferences.gamesDirectory)

    init {
        taskD.addParameters(fileP)
    }

    override fun run() {
        val reader = SGFReader(fileP.value!!)
        val game = reader.read()

        val view = EditGameView(mainWindow, game)
        mainWindow.changeView(view)
        game.rewindTo(game.root)
        // TODO Is this really needed?
        game.apply(game.root)
    }

}
