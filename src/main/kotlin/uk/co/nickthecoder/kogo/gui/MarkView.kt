package uk.co.nickthecoder.kogo.gui

import javafx.scene.control.Label
import uk.co.nickthecoder.kogo.model.Point
import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.kogo.model.LabelMark

/**
 * A single mark on the board (or on top of stones). A child of MarksView.
 */
open class MarkView : Label {

    internal var marksView: MarksView? = null

    var point: Point
        set(v) {
            field = v
            marksView?.let { it.node.requestLayout() }
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

    fun color(color: StoneColor) {
        styleClass.removeAll("black", "white")
        styleClass.add(color.toString().toLowerCase())
    }

}
