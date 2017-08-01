package uk.co.nickthecoder.kogo.shell

import javafx.event.ActionEvent
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.gui.TopLevelView

abstract class GridView(mainWindow: MainWindow, val buttonSize: Double = 150.0) : TopLevelView(mainWindow) {

    val buttons = mutableListOf<Button>()

    val hbox = HBox()

    val vbox = VBox()

    val grid = GridPane()

    override val node: Node = vbox

    abstract val viewStyle: String

    override fun build() {
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

        addButtons()
        buildButtons()
    }

    fun buildButtons() {
        // Arrange the buttons in a square(ish) shape
        val columns = Math.ceil(Math.sqrt(buttons.size.toDouble())).toInt()
        var column = 0
        var row = 0

        buttons.forEach { button ->
            grid.add(button, column, row)
            column++
            if (column >= columns) {
                column = 0
                row++
            }
        }
    }

    abstract fun addButtons()

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
