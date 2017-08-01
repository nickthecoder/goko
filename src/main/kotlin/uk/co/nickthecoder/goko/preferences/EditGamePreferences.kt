package uk.co.nickthecoder.goko.preferences


import uk.co.nickthecoder.goko.gui.ShowBranches
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.enumChoices

class EditGamePreferences : AbstractTask(), CommentsPreferences {

    override val taskD = TaskDescription("editGame")

    val showMoveNumbersP = IntParameter(name = "showMoveNumbers", value = 0)
    override val showNodeAnnotationsP = BooleanParameter(name = "showNodeAnnotations", value = true)
    override val showMoveAnnotationsP = BooleanParameter(name = "showMoveAnnotations", value = true)
    val showBranchesP = ChoiceParameter("showBraches", value = ShowBranches.DO_NOT_SHOW).enumChoices()

    init {
        taskD.addParameters(showMoveNumbersP, showNodeAnnotationsP, showMoveAnnotationsP, showBranchesP)
    }

    override fun run() {
        Preferences.save()
    }
}
