package uk.co.nickthecoder.goko.preferences

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.FileParameter

class JosekiPreferences : AbstractTask(), CommentsPreferences {

    override val taskD = TaskDescription("joseki")

    val josekiDictionaryP = FileParameter(name = "josekiDictionary", extensions = listOf("sgf"), required = false)
    override val showNodeAnnotationsP = BooleanParameter(name = "showNodeAnnotations", value = true)
    override val showMoveAnnotationsP = BooleanParameter(name = "showMoveAnnotations", value = true)

    init {
        taskD.addParameters(josekiDictionaryP, showNodeAnnotationsP, showMoveAnnotationsP)
    }

    override fun run() {
        Preferences.save()
    }
}
