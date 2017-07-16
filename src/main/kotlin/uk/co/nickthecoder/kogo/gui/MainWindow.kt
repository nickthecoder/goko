package uk.co.nickthecoder.kogo.gui

import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.kogo.KoGo
import uk.co.nickthecoder.kogo.shell.Home
import uk.co.nickthecoder.paratask.util.MyTabPane

class MainWindow(val stage: Stage) {

    var tabs = MyTabPane()

    val whole = BorderPane()

    init {
        stage.title = "Kogo"
        val home = Home(this)
        addView(home)

        whole.center = tabs
        stage.scene = Scene(whole, 700.0, 700.0)
        KoGo.style(stage.scene)
        stage.show()
    }

    fun addView(view: View, index: Int = -1) {
        view.build()
        val tab = ViewTab(view)
        if (index < 0) {
            tabs.add(tab)
        } else {
            tabs.add(index, tab)
        }
        tabs.selectedTab = tab
    }

    fun changeView(view: View) {
        val oldTab = tabs.selectionModel.selectedItem
        val index = tabs.selectionModel.selectedIndex

        if (oldTab != null) {
            tabs.remove(oldTab)
        }

        addView(view, index)
    }

    fun remove(view: View) {
        for (tab in tabs.tabs) {
            if (tab is ViewTab && tab.hasView(view)) {
                tabs.remove(tab)
                return
            }
        }
    }

    fun show() {
        stage.show()
        stage.setOnHiding {
            tabs.clear() // Ensure views are tidied up correctly.
        }
    }

    fun hide() {
        stage.hide()
    }
}
