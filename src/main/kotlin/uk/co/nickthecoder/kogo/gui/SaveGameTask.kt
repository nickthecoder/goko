package uk.co.nickthecoder.kogo.gui

import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.SGFWriter
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter

/**
 */
class SaveGameTask(val game: Game) : AbstractTask() {

    override val taskD = TaskDescription("saveGame")

    val fileP = FileParameter("file", value = Preferences.gamesDirectory, mustExist = null)

    init {
        taskD.addParameters(fileP)
        game.file?.let {
            fileP.value = it
        }
    }

    override fun run() {
        val writer = SGFWriter(fileP.value!!)
        writer.write(game)
        game.file = fileP.value
    }
}
