package uk.co.nickthecoder.kogo.gui

import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.GameListener

// TODO Currently this is only used by ProblemView, but later make it more generic, usable when editing and reviewing games.
// This means that that fields should be editable.
// OR, create another version of this class which IS editable.

class CommentsView(override val mainWindow: MainWindow, val game: Game) : View, GameListener {

    override val title = "Comments" // Not used!

    val whole = HBox()

    override val node = whole

    val nameC = TextField()

    val commentC = TextArea()

    val statusesPane = FlowPane()

    override fun build(): View {
        nameC.isEditable = false
        commentC.isEditable = false

        whole.children.addAll(nameC, commentC, statusesPane)

        return this
    }

    override fun moved() {
        with(game.currentNode) {
            nameC.text = name
            commentC.text = comment

            // Icons for "Good for Black/White", "Even", "Hotspot" etc.
            statuses.clear()
            for( status in statuses ) {
                // TODO Add icons to statusesPane
            }
        }
    }
}
