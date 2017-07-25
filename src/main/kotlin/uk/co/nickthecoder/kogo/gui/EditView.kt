package uk.co.nickthecoder.kogo.gui

import javafx.event.ActionEvent
import javafx.scene.control.Button
import uk.co.nickthecoder.kogo.model.Game

class EditView(mainWindow : MainWindow, game: Game) : PlayingView( mainWindow, game) {

    val backB = Button("<")

    val forwardB = Button(">")

    val toStartB = Button("|<")

    val toEndB = Button(">|")

    override fun build() {
        super.build()

        backB.addEventHandler(ActionEvent.ACTION) { game.moveBack() }
        forwardB.addEventHandler(ActionEvent.ACTION) { game.moveForward() }
        toStartB.addEventHandler(ActionEvent.ACTION) { game.moveToStart() }
        toEndB.addEventHandler(ActionEvent.ACTION) { game.moveToEnd() }

        toolBar.items.addAll(toStartB, backB, forwardB, toEndB)
        toolBar.items.removeAll(resignB)
    }

}
