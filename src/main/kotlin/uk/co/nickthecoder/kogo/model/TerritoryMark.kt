package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.gui.SymbolMarkView

class TerritoryMark(point: Point, val color: StoneColor) : SymbolMark(point, "territory", null) {

    override fun createMarkView(): SymbolMarkView {
        val mv = super.createMarkView()
        mv.colorWhite(color == StoneColor.WHITE)
        return mv
    }
}
