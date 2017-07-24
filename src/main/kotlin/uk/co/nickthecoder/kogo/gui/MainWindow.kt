package uk.co.nickthecoder.kogo.gui

import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import uk.co.nickthecoder.kogo.KoGo
import uk.co.nickthecoder.kogo.shell.Home
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.ShortcutHelper
import uk.co.nickthecoder.paratask.gui.MyTabPane

class MainWindow(val stage: Stage) {

    var tabs = MyTabPane<MyTab>()

    val whole = BorderPane()

    val shortcuts = ShortcutHelper("MainWindow", whole)

    init {
        stage.title = "Kogo"
        val home = Home(this)
        addView(home)

        whole.center = tabs
        stage.scene = Scene(whole, 1000.0, 700.0)
        KoGo.style(stage.scene)
        stage.show()

        shortcuts.add(KoGoActions.CLOSE_TAB) {
            if (tabs.selectedTab?.canClose == true) {
                tabs.selectedTab?.remove()
            }
        }
    }

    fun indexOf(view: TopLevelView): Int {
        for (i in 0..tabs.tabs.size - 1) {
            val tab = tabs.tabs[i]
            if (tab is ViewTab && tab.view === view) {
                return i
            }
        }
        return -1
    }

    fun addViewAfter(afterView: TopLevelView, view: TopLevelView, selectTab: Boolean = true) {
        val i = indexOf(afterView)
        if (i < 0) {
            addView(view, selectTab = selectTab)
        } else {
            addView(view, i + 1, selectTab = selectTab)
        }
    }

    fun addView(view: TopLevelView, index: Int = -1, selectTab: Boolean = true) {
        view.build()
        val tab = ViewTab(view)
        if (index < 0) {
            tabs.add(tab)
        } else {
            tabs.add(index, tab)
        }
        if (selectTab) {
            tabs.selectedTab = tab
        }
        if (view is Home) {
            tab.canClose = false
        }
    }

    fun changeView(view: TopLevelView) {
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
