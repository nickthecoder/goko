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
package uk.co.nickthecoder.goko.shell

import javafx.event.ActionEvent
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.gui.TopLevelView

abstract class GridView(mainWindow: MainWindow, val buttonSize: Double = 150.0) : TopLevelView(mainWindow) {

    val buttons = mutableListOf<Button>()

    val whole = BorderPane()

    val hbox = HBox()

    val vbox = VBox()

    val grid = GridPane()

    override val node: Node = whole

    abstract val viewStyle: String

    override fun build() {
        whole.center = vbox

        with(vbox) {
            children.add(hbox)
            alignment = Pos.CENTER
        }
        with(hbox) {
            children.add(grid)
            styleClass.add(viewStyle)
            alignment = Pos.CENTER
        }
        grid.styleClass.add("grid")

        createButtons()
        buildButtons()
    }

    open fun createButtons() {
        buttons.clear()
    }

    fun buildButtons() {
        // Arrange the buttons in a square(ish) shape
        val columns = Math.ceil(Math.sqrt(buttons.size.toDouble())).toInt()
        var column = 0
        var row = 0

        grid.children.clear()

        buttons.forEach { button ->
            grid.add(button, column, row)
            column++
            if (column >= columns) {
                column = 0
                row++
            }
        }
    }

    fun createButton(label: String, style: String?, viewFactory: () -> TopLevelView): Button {
        val button = Button(label)
        with(button) {
            style?.let { styleClass.add(it) }
            wrapTextProperty().value = true
            addEventHandler(ActionEvent.ACTION) {
                mainWindow.addView(viewFactory())
            }
            prefHeight = buttonSize
            prefWidth = buttonSize
        }
        return button
    }

}
