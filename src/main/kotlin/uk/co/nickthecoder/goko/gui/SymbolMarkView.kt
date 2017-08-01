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
package uk.co.nickthecoder.goko.gui

import javafx.css.CssMetaData
import javafx.css.StyleConverter
import javafx.css.Styleable
import javafx.css.StyleableStringProperty
import javafx.scene.control.Label
import uk.co.nickthecoder.goko.model.Point
import uk.co.nickthecoder.goko.model.SymbolMark

class SymbolMarkView : MarkView {

    /**
     * Gets the -fx-symbol property from css.
     */
    var symbolProperty: StyleableStringProperty = object : StyleableStringProperty("") {
        override fun getName() = "symbolProperty"
        override fun getBean() = this@SymbolMarkView

        override fun getCssMetaData(): CssMetaData<SymbolMarkView, String> {
            return SYMBOL_META_DATA
        }

        override fun invalidated() {
            requestLayout()
        }
    }

    constructor(point: Point, style: String) : super(point, style)

    constructor(mark: SymbolMark) : super(mark.point, mark.style)

    init {
        textProperty().bind(symbolProperty)
    }

    override fun getControlCssMetaData(): List<CssMetaData<out Styleable, *>> {
        return cssMetaDataList
    }

    companion object {

        internal val cssMetaDataList = mutableListOf<CssMetaData<out Styleable, *>>()

        internal val SYMBOL_META_DATA = object : CssMetaData<SymbolMarkView, String>("-fx-symbol", StyleConverter.getStringConverter(), "") {
            override fun isSettable(symbolMarkView: SymbolMarkView): Boolean = true

            override fun getStyleableProperty(symbolMarkView: SymbolMarkView): StyleableStringProperty {
                return symbolMarkView.symbolProperty
            }
        }

        init {
            Label.getClassCssMetaData().forEach {
                cssMetaDataList.add(it)
            }
            cssMetaDataList.add(SYMBOL_META_DATA)
        }
    }
}
