package uk.co.nickthecoder.kogo.preferences


import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.currentDirectory
import uk.co.nickthecoder.paratask.util.homeDirectory

class EditGamePreferences : AbstractTask() {

    override val taskD = TaskDescription("editGame")

    val showMoveNumbersP = IntParameter(name = "showMoveNumbers", value = 0)

    init {
        taskD.addParameters(showMoveNumbersP)
    }

    override fun run() {
        Preferences.save()
    }
}
