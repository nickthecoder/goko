package uk.co.nickthecoder.kogo.preferences


import uk.co.nickthecoder.kogo.gui.ShowContinuations
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter

class EditGamePreferences : AbstractTask(), CommentsPreferences {

    override val taskD = TaskDescription("editGame")

    val showMoveNumbersP = IntParameter(name = "showMoveNumbers", value = 0)
    override val showNodeAnotationsP = BooleanParameter(name = "showNodeAnotations", value = true)
    override val showMoveAnotationsP = BooleanParameter(name = "showMoveAnotations", value = true)
    val showContinuationsP = ShowContinuations.createChoices()

    init {
        taskD.addParameters(showMoveNumbersP, showNodeAnotationsP, showMoveAnotationsP, showContinuationsP)
    }

    override fun run() {
        Preferences.save()
    }
}
