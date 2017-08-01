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

import uk.co.nickthecoder.goko.model.timeScales
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.ScaledDoubleParameter
import uk.co.nickthecoder.paratask.parameters.ScaledValue

class AdvancedPreferences : AbstractTask() {

    override val taskD = TaskDescription("advanced")

    val finalScoreTimeoutP = ScaledDoubleParameter("finalScoreTimeout", value = ScaledValue(30.0, 60.0), scales = timeScales)
    val checkGnuGoSyncP = BooleanParameter("checkGnuGoSync", value = false)

    init {
        taskD.addParameters(checkGnuGoSyncP, finalScoreTimeoutP)
    }

    override fun run() {
        Preferences.save()
    }
}
