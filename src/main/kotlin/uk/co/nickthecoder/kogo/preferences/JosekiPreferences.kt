package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.currentDirectory
import uk.co.nickthecoder.paratask.util.homeDirectory

class JosekiPreferences : AbstractTask(), CommentsPreferences {

    override val taskD = TaskDescription("joseki")

    val josekiDictionaryP = FileParameter(name = "josekiDictionary", extensions = listOf("sgf"), required = false)
    override val showNodeAnotationsP = BooleanParameter(name = "showNodeAnotations", value = true)
    override val showMoveAnotationsP = BooleanParameter(name = "showMoveAnotations", value = true)

    init {
        taskD.addParameters(josekiDictionaryP, showNodeAnotationsP, showMoveAnotationsP)
    }

    override fun run() {
        Preferences.save()
    }
}
