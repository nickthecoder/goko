package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter

class ProblemsPreferences : AbstractTask() {

    override val taskD = TaskDescription("problems")

    val showContinuationsP = BooleanParameter("showContinuations", value = true)

    val automaticOpponentP = BooleanParameter("automaticOpponent", value = true, description = "The computer can play the 2nd player's moves")

    init {
        taskD.addParameters(showContinuationsP, automaticOpponentP)
    }

    override fun run() {
        Preferences.save()
    }
}
