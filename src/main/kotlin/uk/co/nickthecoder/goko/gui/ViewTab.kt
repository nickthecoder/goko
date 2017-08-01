package uk.co.nickthecoder.goko.gui

import uk.co.nickthecoder.paratask.gui.MyTab

class ViewTab(val view: TopLevelView) : MyTab(view.title, view.node) {

    override fun removed() {
        view.tidyUp()
        if (view.mainWindow.tabs.tabs.isEmpty()) {
            view.mainWindow.hide()
        }
    }

    fun hasView(view: View): Boolean = this.view.hasView(view)
}
