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

import javafx.geometry.Side
import javafx.scene.Node
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.gui.TopLevelView
import uk.co.nickthecoder.goko.gui.View
import uk.co.nickthecoder.goko.gui.ViewTab
import uk.co.nickthecoder.goko.shell.PromptTaskView
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.MyTabPane

class PreferencesView(mainWindow: MainWindow, val initialPage: Task? = null) : TopLevelView(mainWindow) {

    private val tabs = MyTabPane<ViewTab>()

    override val node: Node = tabs

    override val title = "Preferences"

    init {
        tabs.side = Side.BOTTOM
    }

    override fun build() {

        for (task in Preferences.preferenceTasksMap.values) {
            val view = object : PromptTaskView(task, mainWindow) {
                override fun onOk() {
                    super.onOk()
                    mainWindow.remove(this)
                }
            }
            view.build()
            val tab = ViewTab(view)
            tabs.add(tab)
            if (task === initialPage) {
                tabs.selectedTab = tab
            }
        }
    }

    override fun hasView(view: View): Boolean {
        return if (tabs.tabs.any { it.hasView(view) }) true else super.hasView(view)
    }
}
