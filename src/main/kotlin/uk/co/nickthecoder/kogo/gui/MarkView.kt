package uk.co.nickthecoder.kogo.gui

import javafx.scene.control.Label
import uk.co.nickthecoder.kogo.model.LabelMark
import uk.co.nickthecoder.kogo.model.Point

/**
 * A single mark on the board (or on top of stones). A child of MarksView.
 */
open class MarkView : Label {

    internal var marksView: MarksView? = null

    var point: Point
        set(v) {
            field = v
            marksView?.node?.requestLayout()
        }

    constructor(point: Point, style: String? = null, text: String = "") : super(text) {
        this.point = point
        style?.let { styleClass.add(it) }
    }

    constructor(mark: LabelMark) : super(mark.text) {
        this.point = mark.point
        styleClass.add(mark.style)
    }

    init {
        styleClass.add("mark")
    }

    fun colorWhite(white: Boolean) {
        styleClass.removeAll("black", "white")
        styleClass.add(if (white) "white" else "black")
    }

}
