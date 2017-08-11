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

import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import uk.co.nickthecoder.goko.GoKo
import uk.co.nickthecoder.goko.model.SGFReader
import uk.co.nickthecoder.goko.preferences.Preferences
import uk.co.nickthecoder.goko.preferences.PreferencesView
import uk.co.nickthecoder.goko.shell.Home
import uk.co.nickthecoder.paratask.gui.MyTab
import uk.co.nickthecoder.paratask.gui.MyTabPane
import uk.co.nickthecoder.paratask.gui.ShortcutHelper

class MainWindow(val stage: Stage) {

    var tabs = MyTabPane<MyTab>()

    val whole = BorderPane()

    val shortcuts = ShortcutHelper("MainWindow", whole, filter = false)

    init {
        stage.title = "GoKo"
        val home = Home(this)
        home.build()
        addView(home)

        whole.center = tabs
        stage.scene = Scene(whole, 1000.0, 800.0)
        GoKo.style(stage.scene)
        stage.show()

        shortcuts.add(GoKoActions.CLOSE_TAB) {
            if (tabs.selectedTab?.canClose == true) {
                tabs.selectedTab?.close()
            }
        }
        shortcuts.add(GoKoActions.OPEN_GAME_FILE) { onOpenFile() }
        shortcuts.add(GoKoActions.PREFERENCES) { onPreferences() }
        shortcuts.add(GoKoActions.JOSEKI_DICTIONARY) { openJosekiDictionary() }
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

    fun onOpenFile() {

        val fileChooser = FileChooser()
        fileChooser.title = "Open SGF File"
        fileChooser.extensionFilters.add(FileChooser.ExtensionFilter("Smart Game Format", "*.sgf"))
        fileChooser.initialDirectory = Preferences.gamesDirectory

        val file = fileChooser.showOpenDialog(Stage())
        file?.let {
            val reader = SGFReader(file)
            val game = reader.read()

            val view = EditGameView(this, game)
            view.build()
            addView(view)
            game.rewindTo(game.root)
            // TODO Is this really needed?
            game.apply(game.root)

        }
    }

    fun onPreferences() {
        val view = PreferencesView(this)
        view.build()
        addView(view)
    }

    fun openJosekiDictionary() {

        val josekiFile = Preferences.josekiDirectionary
        val view = if (josekiFile != null) {
            JosekiView(this, josekiFile)
        } else {
            PreferencesView(this, Preferences.josekiPreferences)
        }
        view.build()
        addView(view)
    }
}
