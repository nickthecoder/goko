package uk.co.nickthecoder.kogo.gui

import javafx.event.ActionEvent
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import uk.co.nickthecoder.kogo.KoGo
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.kogo.preferences.CommentsPreferences
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.kogo.preferences.PreferencesListener

class CommentsView(val game: Game, val readOnly: Boolean, val preferences: CommentsPreferences) : View, GameListener, PreferencesListener {

    val whole = VBox()

    override val node = whole

    val nameC = TextField()

    val commentC = TextArea()

    val passedLabel = Label("Passed")

    val moveAnotationsPane = FlowPane()

    val nodeAnotationsPane = FlowPane()

    val moveAnotationButtons = mutableMapOf<MoveAnotation, ToggleButton>()

    val nodeAnotationButtons = mutableMapOf<NodeAnotation, Button>()

    override fun build() {

        // Move anotations (good, doubtful, bad, interesting)
        moveAnotationsPane.styleClass.add("anotations")
        val moveToggleGroup = ToggleGroup()
        for (anotation in MoveAnotation.values()) {
            val name = anotation.toString().toLowerCase()
            val img = KoGo.imageResource("buttons/move-${name}.png")
            val button = ToggleButton("", ImageView(img))
            button.tooltip = Tooltip(name.capitalize())
            moveAnotationButtons.put(anotation, button)
            if (readOnly) {
                button.isDisable = true
            } else {
                moveAnotationsPane.children.add(button)
                button.addEventHandler(ActionEvent.ACTION) {
                    game.currentNode.moveAnotation = if (button.isSelected) anotation else null
                }
            }
            moveToggleGroup.toggles.add(button)
        }

        // Node anotations (Even, good for B/W, Hotspot, Unclear)
        nodeAnotationsPane.styleClass.add("anotations")
        for (anotation in NodeAnotation.values()) {
            val name = anotation.toString().toLowerCase().replace('_', '-')
            val img = KoGo.imageResource("buttons/node-${name}.png")
            val button = Button("", ImageView(img))
            button.tooltip = Tooltip(name.capitalize().replace("-", " "))
            nodeAnotationButtons.put(anotation, button)
            if (readOnly) {
                button.isDisable = true
            } else {
                nodeAnotationsPane.children.add(button)
                button.addEventHandler(ActionEvent.ACTION) {
                    changeNodeAnotation(anotation)
                }
            }
        }

        // TODO Do a similar thing for NodeAnotations

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

        whole.children.addAll(Label("Node"), nameC, Label("Comment"), commentC, passedLabel, nodeAnotationsPane, moveAnotationsPane)

        update()
        game.listeners.add(this)
        Preferences.listeners.add(this)
    }

    override fun tidyUp() {
        Preferences.listeners.remove(this)
    }

    fun changeNodeAnotation(anotation: NodeAnotation) {
        val node = game.currentNode
        if (node.nodeAnotation != anotation) {
            node.nodeAnotation = anotation
            node.nodeAnotationVery = false
        } else {
            if (!node.nodeAnotationVery) {
                node.nodeAnotationVery = true
            } else {
                node.nodeAnotation = null
                node.nodeAnotationVery = false
            }
        }
        updateNodeAnotations()
    }

    fun updateNodeAnotations() {
        nodeAnotationsPane.isVisible = preferences.showNodeAnotationsP.value == true

        val node = game.currentNode
        val anotation = node.nodeAnotation
        val very = node.nodeAnotationVery
        if (readOnly) {
            nodeAnotationsPane.children.clear()
            if (anotation != null) {
                val button = nodeAnotationButtons[anotation]!!
                nodeAnotationsPane.children.add(button)
                if (very) {
                    button.styleClass.remove("very")
                    button.styleClass.add("very")
                } else {
                    button.styleClass.remove("very")
                }
            }
        } else {
            nodeAnotationButtons.forEach { an, button ->
                if (an == anotation) {
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

    fun updateMoveAnotations() {
        val node = game.currentNode
        if (node is SetupNode || preferences.showMoveAnotationsP.value == false) {
            moveAnotationsPane.isVisible = false
        } else {
            moveAnotationsPane.isVisible = true

            if (readOnly) {
                moveAnotationsPane.children.clear()
                node.moveAnotation?.let {
                    moveAnotationsPane.children.add(moveAnotationButtons[it])
                }
            } else {
                for (an in MoveAnotation.values()) {
                    val button = moveAnotationButtons[an]!!
                    button.isSelected = node.moveAnotation == an
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
        updateMoveAnotations()
        updateNodeAnotations()
    }

    override fun moved() {
        update()
    }

    override fun updatedCurrentNode() {
        update()
    }

    override fun preferencesChanged() {
        updatedCurrentNode()
    }
}
