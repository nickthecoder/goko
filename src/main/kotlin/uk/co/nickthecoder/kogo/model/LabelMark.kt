package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.gui.MarkView

class LabelMark(point: Point, val text: String) : Mark(point, "label-mark") {
    override fun createMarkView() = MarkView(this)

    override fun toString() = "Label @ $point = '$text'"
}
