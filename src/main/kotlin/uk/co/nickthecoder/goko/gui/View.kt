package uk.co.nickthecoder.goko.gui

import javafx.scene.Node

interface View {

    val node: Node

    fun build()

    fun tidyUp() {}

    fun hasView(view: View): Boolean {
        return view === this
    }
}
