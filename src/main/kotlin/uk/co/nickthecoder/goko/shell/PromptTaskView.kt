package uk.co.nickthecoder.goko.shell

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.gui.TopLevelView
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm

open class PromptTaskView(val task: Task, mainWindow: MainWindow) : TopLevelView(mainWindow) {

    override val title = task.taskD.label

    val borderPane = BorderPane()

    override val node: Node = borderPane

    val taskForm = TaskForm(task)

    private val buttons = FlowPane()

    val okButton = Button("Ok")

    val cancelButton = Button("Cancel")

    override fun build() {
        with(borderPane) {
            styleClass.add("prompt")
            center = taskForm.scrollPane
            bottom = buttons
        }

        with(cancelButton) {
            cancelButton.onAction = EventHandler { onCancel() }
            cancelButton.isCancelButton = true
        }

        with(okButton) {
            okButton.onAction = EventHandler { onOk() }
            okButton.isDefaultButton = true
        }


        with(buttons) {
            children.addAll(okButton, cancelButton)
            styleClass.add("buttons")
        }
    }

    private fun onCancel() {
        mainWindow.remove(this)
    }

    protected open fun onOk() {
        taskForm.check()
        task.run()
    }
}
