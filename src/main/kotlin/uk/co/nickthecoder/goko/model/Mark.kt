/*
GoKo a Go Client
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.goko.model

import uk.co.nickthecoder.goko.gui.MarkView
import uk.co.nickthecoder.goko.gui.SymbolMarkView

abstract class Mark(val point: Point, val style: String) {
    abstract fun createMarkView(): MarkView
}

abstract class SymbolMark(point: Point, style: String) : Mark(point, style) {

    override fun createMarkView() = SymbolMarkView(this)
}

class MainLineMark(point: Point) : SymbolMark(point, "main-line")

class AlternateMark(point: Point) : SymbolMark(point, "alternate")


class CircleMark(point: Point) : SymbolMark(point, "circle")

class SquareMark(point: Point) : SymbolMark(point, "square")

class CrossMark(point: Point) : SymbolMark(point, "cross")

class TriangleMark(point: Point) : SymbolMark(point, "triangle")


class DeadMark(point: Point) : SymbolMark(point, "dead")

class QuestionMark(point: Point) : SymbolMark(point, "question")

class TerritoryMark(point: Point, val color: StoneColor) : SymbolMark(point, "territory") {

    override fun createMarkView(): SymbolMarkView {
        val mv = super.createMarkView()
        mv.colorWhite(color == StoneColor.WHITE)
        return mv
    }
}

class LabelMark(point: Point, val text: String) : Mark(point, "label-mark") {

    override fun createMarkView() = MarkView(this)

    override fun toString() = "Label @ $point = '$text'"
}
