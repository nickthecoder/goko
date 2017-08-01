package uk.co.nickthecoder.goko.preferences

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.currentDirectory
import uk.co.nickthecoder.paratask.util.homeDirectory

class BasicPreferences : AbstractTask() {

    override val taskD = TaskDescription("basic")

    val yourNameP = StringParameter("yourName", required = false)
    val yourRankP = StringParameter("yourRank", required = false, description = "e.g. 8k, 2d or 1p")
    val gamesDirectoryP = FileParameter("gamesDirectory", expectFile = false, value = currentDirectory)
    val playSoundsP = BooleanParameter("playSounds", value = true)

    init {
        taskD.addParameters(yourNameP, yourRankP, gamesDirectoryP, playSoundsP)
        yourNameP.value = homeDirectory.name
    }

    override fun run() {
        Preferences.save()
    }
}
