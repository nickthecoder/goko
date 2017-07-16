package uk.co.nickthecoder.kogo.gui

import javafx.scene.Node

interface View {

    val mainWindow: MainWindow

    val node: Node

    val title: String

    fun build(): View

    fun tidyUp() {}

    open fun hasView(view: View): Boolean {
        return view === this
    }
}
