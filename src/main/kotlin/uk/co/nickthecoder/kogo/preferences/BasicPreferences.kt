package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.currentDirectory
import uk.co.nickthecoder.paratask.util.homeDirectory

class BasicPreferences : AbstractTask() {

    override val taskD = TaskDescription("basic")

    val yourNameP = StringParameter("yourName", required = false)

    val yourRankP = StringParameter("yourRank", required = false, description = "e.g. 8k, 2d or 1p")

    val gamesDirectoryP = FileParameter("gamesDirectory", expectFile = false, value = currentDirectory)

    init {
        taskD.addParameters(yourNameP, yourRankP, gamesDirectoryP)
        yourNameP.value = homeDirectory.name
    }

    override fun run() {
        Preferences.save()
    }
}
