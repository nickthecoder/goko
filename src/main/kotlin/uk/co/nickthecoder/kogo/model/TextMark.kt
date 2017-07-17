package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.gui.MarkView

class TextMark(point: Point, style: String, val text: String = "") : Mark(point, style) {
    override fun createMarkView() = MarkView(this)
}
