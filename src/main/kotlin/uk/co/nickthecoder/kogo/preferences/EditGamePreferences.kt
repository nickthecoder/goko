package uk.co.nickthecoder.kogo.preferences


import uk.co.nickthecoder.kogo.gui.ShowBranches
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.enumChoices

class EditGamePreferences : AbstractTask(), CommentsPreferences {

    override val taskD = TaskDescription("editGame")

    val showMoveNumbersP = IntParameter(name = "showMoveNumbers", value = 0)
    override val showNodeAnotationsP = BooleanParameter(name = "showNodeAnotations", value = true)
    override val showMoveAnotationsP = BooleanParameter(name = "showMoveAnotations", value = true)
    val showBranchesP = ChoiceParameter("showBraches", value = ShowBranches.DO_NOT_SHOW).enumChoices()

    init {
        taskD.addParameters(showMoveNumbersP, showNodeAnotationsP, showMoveAnotationsP, showBranchesP)
    }

    override fun run() {
        Preferences.save()
    }
}
