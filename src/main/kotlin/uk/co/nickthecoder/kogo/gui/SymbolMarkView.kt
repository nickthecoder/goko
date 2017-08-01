package uk.co.nickthecoder.kogo.gui

import javafx.css.CssMetaData
import javafx.css.StyleConverter
import javafx.css.Styleable
import javafx.css.StyleableStringProperty
import javafx.scene.control.Label
import uk.co.nickthecoder.kogo.model.Point
import uk.co.nickthecoder.kogo.model.SymbolMark

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
