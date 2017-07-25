package uk.co.nickthecoder.kogo.gui

import javafx.scene.Node

interface View {

    val node: Node

    fun build()

    fun tidyUp() {}

    open fun hasView(view: View): Boolean {
        return view === this
    }
}
