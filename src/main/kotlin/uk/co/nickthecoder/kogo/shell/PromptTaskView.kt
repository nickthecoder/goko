package uk.co.nickthecoder.kogo.shell

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.gui.View
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.fields.TaskForm

open class PromptTaskView(val task: Task, override val mainWindow: MainWindow) : View {

    override val title = task.taskD.label

    val borderPane = BorderPane()

    override val node: Node = borderPane

    val taskForm = TaskForm(task)

    private val buttons = FlowPane()

    val okButton = Button("Ok")

    val cancelButton = Button("Cancel")

    override fun build(): PromptTaskView {
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

        return this
    }

    private fun onCancel() {
        mainWindow.remove(this)
    }

    protected open fun onOk() {
        taskForm.check()
        task.run()
    }
}
