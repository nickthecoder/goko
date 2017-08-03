package uk.co.nickthecoder.goko.shell

import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.paratask.Task

class TaskView(task: Task, mainWindow: MainWindow) : PreferencesTaskView(task, mainWindow) {

    val borderPane = BorderPane()

    override val node = borderPane

    private val buttons = FlowPane()

    val okButton = Button("Ok")

    val cancelButton = Button("Cancel")

    override fun build() {
        super.build()

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

    protected fun onOk() {
        if (taskForm.check()) {
            task.run()
        }
    }

}
