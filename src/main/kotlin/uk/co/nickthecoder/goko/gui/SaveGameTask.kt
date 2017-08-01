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
package uk.co.nickthecoder.goko.gui

import uk.co.nickthecoder.goko.model.Game
import uk.co.nickthecoder.goko.model.SGFWriter
import uk.co.nickthecoder.goko.preferences.Preferences
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter

/**
 */
class SaveGameTask(val game: Game) : AbstractTask() {

    override val taskD = TaskDescription("saveGame")

    val fileP = FileParameter("file", value = Preferences.gamesDirectory, mustExist = null)

    init {
        taskD.addParameters(fileP)
        game.file?.let {
            fileP.value = it
        }
    }

    override fun run() {
        val writer = SGFWriter(fileP.value!!)
        writer.write(game)
        game.file = fileP.value
    }
}
