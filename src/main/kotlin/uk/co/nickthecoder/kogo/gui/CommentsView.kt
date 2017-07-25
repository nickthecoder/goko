package uk.co.nickthecoder.kogo.gui

import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.GameListener

// TODO Currently this is only used by ProblemView, but later make it more generic, usable when editing and reviewing games.
// This means that that fields should be editable.
// OR, create another version of this class which IS editable.

class CommentsView(val game: Game) : View, GameListener {

    val whole = VBox()

    override val node = whole

    val nameC = TextField()

    val commentC = TextArea()

    val statusesPane = FlowPane()

    override fun build() {
        whole.styleClass.add("comments")

        nameC.isEditable = false
        with(commentC) {
            isEditable = false
            isWrapText = true
            prefRowCount = 20
        }
        whole.children.addAll(Label("Node"), nameC, Label("Comment"), commentC, statusesPane)

        update()
        game.gameListeners.add(this)
    }

    fun update() {
        with(game.currentNode) {
            nameC.text = name
            commentC.text = comment

            // Icons for "Good for Black/White", "Even", "Hotspot" etc.
            statuses.clear()
            for (status in statuses) {
                // TODO Add icons to statusesPane
            }
        }
    }

    override fun moved() {
        update()
    }

    override fun updatedCurrentNode() {
        update()
    }
}
