package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.gui.SymbolMarkView

open class SymbolMark(point: Point, style: String, sgfCode: String?) : Mark(point, style) {

    override fun createMarkView() = SymbolMarkView(this)
}
