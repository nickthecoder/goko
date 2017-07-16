package uk.co.nickthecoder.kogo.gui

import javafx.css.CssMetaData
import javafx.css.StyleConverter
import javafx.css.Styleable
import javafx.css.StyleableStringProperty
import javafx.scene.control.Label
import uk.co.nickthecoder.kogo.model.Point

class SymbolMark(point: Point, style: String) : MarkView(point, style) {

    /**
     * Gets the -fx-symbol property from css.
     */
    var symbolProperty: StyleableStringProperty = object : StyleableStringProperty("") {
        override fun getName() = "symbolProperty"
        override fun getBean() = this@SymbolMark

        override fun getCssMetaData(): CssMetaData<SymbolMark, String> {
            return SYMBOL_META_DATA
        }

        override fun invalidated() {
            requestLayout()
        }
    }

    init {
        textProperty().bind(symbolProperty)
    }

    override fun getControlCssMetaData(): List<CssMetaData<out Styleable, *>> {
        return cssMetaDataList
    }

    companion object {

        internal val cssMetaDataList = mutableListOf<CssMetaData<out Styleable, *>>()

        internal val SYMBOL_META_DATA = object : CssMetaData<SymbolMark, String>("-fx-symbol", StyleConverter.getStringConverter(), "") {
            override fun isSettable(symbolMark: SymbolMark): Boolean = true

            override fun getStyleableProperty(symbolMark: SymbolMark): StyleableStringProperty {
                return symbolMark.symbolProperty
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
