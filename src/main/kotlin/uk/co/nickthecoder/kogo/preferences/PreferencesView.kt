package uk.co.nickthecoder.kogo.preferences

import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.gui.TopLevelView
import uk.co.nickthecoder.kogo.gui.View
import uk.co.nickthecoder.kogo.gui.ViewTab
import uk.co.nickthecoder.kogo.shell.Home
import uk.co.nickthecoder.kogo.shell.PromptTaskView
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.util.MyTabPane

class PreferencesView(mainWindow: MainWindow, val initialPage: Task? = null) : TopLevelView(mainWindow) {

    private val tabs = MyTabPane()

    override val node: Node = tabs

    override val title = "Preferences"

    init {
        tabs.side = Side.BOTTOM
    }

    override fun build(): PreferencesView {

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
            if ( task === initialPage ) {
                tabs.selectedTab = tab
            }
        }

        return this
    }

    override fun hasView(view: View): Boolean {
        for (tab in tabs.tabs) {
            if (tab is ViewTab && tab.hasView(view)) {
                return true
            }
        }
        return super.hasView(view)
    }
}
