package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.util.currentDirectory

class ProblemsPreferences : AbstractTask() {

    override val taskD = TaskDescription("problems")

    val directoryP = FileParameter("directory", expectFile = false, value = currentDirectory)

    val showContinuationsP = BooleanParameter("showContinuations", value = true)

    val automaticOpponentP = BooleanParameter("automaticOpponent", value = true, description = "The computer can play the 2nd player's moves")

    init {
        taskD.addParameters(directoryP, showContinuationsP, automaticOpponentP)
    }

    override fun run() {
        Preferences.save()
    }
}
