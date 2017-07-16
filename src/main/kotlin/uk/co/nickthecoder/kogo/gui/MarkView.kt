package uk.co.nickthecoder.kogo.gui

import javafx.scene.control.Label
import uk.co.nickthecoder.kogo.model.Point
import uk.co.nickthecoder.kogo.model.StoneColor

/**
 * A single mark on the board (or on top of stones). A child of MarksView.
 */
open class MarkView(point: Point, style: String? = null, text: String = "") : Label(text) {

    internal var marksView: MarksView? = null

    init {
        styleClass.add("mark")
        style?.let { styleClass.add(it) }
    }

    var point: Point = point
        set(v) {
            field = v
            marksView?.let { it.node.requestLayout() }
        }

    fun color(color: StoneColor) {
        styleClass.removeAll("black", "white")
        styleClass.add(color.toString().toLowerCase())
    }

}
