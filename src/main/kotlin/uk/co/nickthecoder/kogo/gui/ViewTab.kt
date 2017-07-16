package uk.co.nickthecoder.kogo.gui

import uk.co.nickthecoder.paratask.util.MyTab

class ViewTab(val view: View) : MyTab(view.title, view.node) {

    override fun removed() {
        view.tidyUp()
        if (view.mainWindow.tabs.tabs.isEmpty()) {
            view.mainWindow.hide()
        }
    }

    fun hasView(view: View): Boolean = this.view.hasView(view)
}
