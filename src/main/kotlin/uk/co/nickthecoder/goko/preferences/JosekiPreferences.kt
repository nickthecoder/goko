/*
GoKo a Go Client
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
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
