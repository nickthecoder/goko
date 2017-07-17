package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.gui.MarkView

abstract class Mark(val point: Point, val style: String) {
    abstract fun createMarkView() : MarkView
}
