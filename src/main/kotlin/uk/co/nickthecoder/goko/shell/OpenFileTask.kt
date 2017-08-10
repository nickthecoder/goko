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
package uk.co.nickthecoder.goko.shell

import uk.co.nickthecoder.goko.gui.EditGameView
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.model.SGFReader
import uk.co.nickthecoder.goko.preferences.Preferences
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.FileParameter

class OpenFileTask(val mainWindow: MainWindow) : AbstractTask() {

    override val taskD = TaskDescription("openFile", "Open an SGF File")

    val fileP = FileParameter("file", value = Preferences.gamesDirectory)

    init {
        taskD.addParameters(fileP)
    }

    override fun run() {
        val reader = SGFReader(fileP.value!!)
        val game = reader.read()

        val view = EditGameView(mainWindow, game)
        view.build()
        mainWindow.changeView(view)
        game.rewindTo(game.root)
        // TODO Is this really needed?
        game.apply(game.root)
    }

}
