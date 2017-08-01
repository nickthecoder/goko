package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.util.currentDirectory

class ProblemsPreferences : AbstractTask(), CommentsPreferences {

    override val taskD = TaskDescription("problems")

    val directoryP = FileParameter("directory", expectFile = false, value = currentDirectory, required = false)
    val showBranchesP = BooleanParameter("showBranches", value = true)
    val automaticOpponentP = BooleanParameter("automaticOpponent", value = true, description = "The computer can play the 2nd player's moves")
    override val showNodeAnnotationsP = BooleanParameter(name = "showNodeAnnotations", value = true)
    override val showMoveAnnotationsP = BooleanParameter(name = "showMoveAnnotations", value = true)

    init {
        taskD.addParameters(directoryP, showBranchesP, automaticOpponentP, showNodeAnnotationsP, showMoveAnnotationsP)
    }

    override fun run() {
        Preferences.save()
    }
}
