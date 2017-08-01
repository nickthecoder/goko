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
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.util.currentDirectory
import uk.co.nickthecoder.paratask.util.homeDirectory

class BasicPreferences : AbstractTask() {

    override val taskD = TaskDescription("basic")

    val yourNameP = StringParameter("yourName", required = false)
    val yourRankP = StringParameter("yourRank", required = false, description = "e.g. 8k, 2d or 1p")
    val gamesDirectoryP = FileParameter("gamesDirectory", expectFile = false, value = currentDirectory)
    val playSoundsP = BooleanParameter("playSounds", value = true)

    init {
        taskD.addParameters(yourNameP, yourRankP, gamesDirectoryP, playSoundsP)
        yourNameP.value = homeDirectory.name
    }

    override fun run() {
        Preferences.save()
    }
}
