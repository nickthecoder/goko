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

import javafx.event.ActionEvent
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.goko.GoKo
import uk.co.nickthecoder.goko.model.*
import uk.co.nickthecoder.goko.preferences.CommentsPreferences
import uk.co.nickthecoder.goko.preferences.Preferences
import uk.co.nickthecoder.goko.preferences.PreferencesListener

class CommentsView(val game: Game, val readOnly: Boolean, val preferences: CommentsPreferences) : View, GameListener, PreferencesListener {

    val whole = VBox()

    override val node = whole

    private val nameC = TextField()

    private val commentC = TextArea()

    private val passedLabel = Label("Passed")

    private val moveAnnotationsPane = FlowPane()

    private val nodeAnnotationsPane = FlowPane()

    private val moveAnnotationButtons = mutableMapOf<MoveAnnotation, ToggleButton>()

    private val nodeAnnotationButtons = mutableMapOf<NodeAnnotation, Button>()

    /**
     * Note, this class does NOT listen for gameMessage events directly, the PlayingView/EditGameView etc does,
     * and may forward the message here. This is because EditGameView has two parts, both of which are capable of
     * displaying messages, and only ONE of them should.
     */
    private val messageLabel = Label()

    override fun build() {

        // Move annotations (good, doubtful, bad, interesting)
        moveAnnotationsPane.styleClass.add("annotations")
        val moveToggleGroup = ToggleGroup()
        for (annotation in MoveAnnotation.values()) {
            val name = annotation.toString().toLowerCase()
            val img = GoKo.imageResource("buttons/move-$name.png")
            val button = ToggleButton("", ImageView(img))
            button.tooltip = Tooltip(name.capitalize())
            moveAnnotationButtons.put(annotation, button)
            if (readOnly) {
                button.isDisable = true
            } else {
                moveAnnotationsPane.children.add(button)
                button.addEventHandler(ActionEvent.ACTION) {
                    game.currentNode.moveAnnotation = if (button.isSelected) annotation else null
                }
            }
            moveToggleGroup.toggles.add(button)
        }

        // Node annotations (Even, good for B/W, Hotspot, Unclear)
        nodeAnnotationsPane.styleClass.add("annotations")
        for (annotation in NodeAnnotation.values()) {
            val name = annotation.toString().toLowerCase().replace('_', '-')
            val img = GoKo.imageResource("buttons/node-$name.png")
            val button = Button("", ImageView(img))
            button.tooltip = Tooltip(name.capitalize().replace("-", " "))
            nodeAnnotationButtons.put(annotation, button)
            if (readOnly) {
                button.isDisable = true
            } else {
                nodeAnnotationsPane.children.add(button)
                button.addEventHandler(ActionEvent.ACTION) {
                    changeNodeAnnotation(annotation)
                }
            }
        }

        whole.styleClass.add("comments")

        if (readOnly) {
            nameC.isEditable = false
            commentC.isEditable = false
        } else {
            nameC.textProperty().addListener({ _, _, value -> game.currentNode.name = value })
            commentC.textProperty().addListener({ _, _, value -> game.currentNode.comment = value })
        }

        with(commentC) {
            isWrapText = true
            prefRowCount = 20
        }

        with(passedLabel) {
            isVisible = false
            styleClass.add("passed")
        }

        messageLabel.styleClass.add("message")
        messageLabel.isWrapText = true

        whole.children.addAll(Label("Node"), nameC, Label("Comment"), commentC, passedLabel, nodeAnnotationsPane, moveAnnotationsPane, messageLabel)

        update()
        game.listeners.add(this)
        Preferences.listeners.add(this)
    }

    override fun tidyUp() {
        Preferences.listeners.remove(this)
    }

    fun changeNodeAnnotation(annotation: NodeAnnotation) {
        val node = game.currentNode
        if (node.nodeAnnotation != annotation) {
            node.nodeAnnotation = annotation
            node.nodeAnnotationVery = false
        } else {
            if (!node.nodeAnnotationVery) {
                node.nodeAnnotationVery = true
            } else {
                node.nodeAnnotation = null
                node.nodeAnnotationVery = false
            }
        }
        updateNodeAnnotations()
    }

    fun updateNodeAnnotations() {
        nodeAnnotationsPane.isVisible = preferences.showNodeAnnotationsP.value == true

        val node = game.currentNode
        val annotation = node.nodeAnnotation
        val very = node.nodeAnnotationVery
        if (readOnly) {
            nodeAnnotationsPane.children.clear()
            if (annotation != null) {
                val button = nodeAnnotationButtons[annotation]!!
                nodeAnnotationsPane.children.add(button)
                if (very) {
                    button.styleClass.remove("very")
                    button.styleClass.add("very")
                } else {
                    button.styleClass.remove("very")
                }
            }
        } else {
            nodeAnnotationButtons.forEach { an, button ->
                if (an == annotation) {
                    button.styleClass.remove("selected")
                    button.styleClass.add("selected")
                    if (very) {
                        button.styleClass.remove("very")
                        button.styleClass.add("very")
                    } else {
                        button.styleClass.remove("very")
                    }
                } else {
                    button.styleClass.removeAll("very", "selected")
                }
            }
        }
    }

    fun updateMoveAnnotations() {
        val node = game.currentNode
        if (node is SetupNode || preferences.showMoveAnnotationsP.value == false) {
            moveAnnotationsPane.isVisible = false
        } else {
            moveAnnotationsPane.isVisible = true

            if (readOnly) {
                moveAnnotationsPane.children.clear()
                node.moveAnnotation?.let {
                    moveAnnotationsPane.children.add(moveAnnotationButtons[it])
                }
            } else {
                for (an in MoveAnnotation.values()) {
                    val button = moveAnnotationButtons[an]!!
                    button.isSelected = node.moveAnnotation == an
                }
            }
        }
    }

    fun update() {
        with(game.currentNode) {
            nameC.text = name
            commentC.text = comment

            passedLabel.isVisible = game.currentNode is PassNode
        }
        updateMoveAnnotations()
        updateNodeAnnotations()
    }

    override fun madeMove(gameNode: GameNode) {
        update()
    }

    override fun undoneMove(gameNode: GameNode) {
        update()
    }

    override fun nodeChanged(node: GameNode) {
        if (node === game.currentNode) {
            update()
        }
    }

    override fun preferencesChanged() {
        update()
    }

    fun message(message: String) {
        messageLabel.text = message
    }
}
