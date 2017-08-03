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

import javafx.event.EventHandler
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.gui.TopLevelView
import uk.co.nickthecoder.goko.gui.View
import uk.co.nickthecoder.goko.gui.ViewTab
import uk.co.nickthecoder.goko.shell.PreferencesTaskView
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.gui.MyTabPane

class PreferencesView(mainWindow: MainWindow, val initialPage: Task? = null) : TopLevelView(mainWindow) {

    private val borderPane = BorderPane()

    private val buttons = FlowPane()

    val okButton = Button("Ok")

    val cancelButton = Button("Cancel")

    private val tabs = MyTabPane<ViewTab>()

    override val node: Node = borderPane

    override val title = "Preferences"

    private val taskViews = mutableListOf<PreferencesTaskView>()

    init {
        tabs.side = Side.BOTTOM
    }

    override fun build() {

        with(borderPane) {
            styleClass.add("prompt")
            center = tabs
            bottom = buttons
        }

        with(cancelButton) {
            cancelButton.onAction = EventHandler { onCancel() }
            cancelButton.isCancelButton = true
        }

        with(okButton) {
            okButton.onAction = EventHandler { onOk() }
            okButton.isDefaultButton = true
        }


        with(buttons) {
            children.addAll(okButton, cancelButton)
            styleClass.add("buttons")
        }

        for (task in Preferences.preferenceTasksMap.values) {
            val view = PreferencesTaskView(task, mainWindow)
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


    private fun onCancel() {
        mainWindow.remove(this)
    }

    protected fun onOk() {
        tabs.tabs.forEach() { tab ->
            val view = tab.view
            if (view is PreferencesTaskView) {
                if (!view.taskForm.check()) {
                    tabs.selectedTab = tab
                    return
                }
            }
        }
        Preferences.save()
        mainWindow.remove(this)
    }
}
