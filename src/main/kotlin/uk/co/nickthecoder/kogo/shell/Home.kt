package uk.co.nickthecoder.kogo.shell

import javafx.scene.control.Button
import uk.co.nickthecoder.kogo.gui.JosekiView
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.preferences.*
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.parameters.TaskParameter

class Home(mainWindow: MainWindow) : GridView(mainWindow, 210.0) {

    override val title = "Home"

    override val viewStyle = "home"

    override fun addButtons() {

        Preferences.gamesPreferences.gamesP.value.forEach { compound ->
            val taskParameter = compound.find("type") as TaskParameter
            val labelP = compound.find("label") as StringParameter
            val task = taskParameter.value
            if (task is AbstractGamePreferences) {
                buttons.add(createTaskButton(labelP.value, style = task.style) { task.createLaunchTask(mainWindow) })
            }
        }

        with(buttons) {

            add(createButton("Problems", "problems") {
                val problemsDir = Preferences.problemsDirectory
                if (problemsDir != null) {
                    ProblemsView(mainWindow)
                } else {
                    PreferencesView(mainWindow, Preferences.problemsPreferences)
                }
            })
            add(createButton("Joseki Dictionary", "joseki") {
                val joseki = Preferences.josekiDirectionary
                if (joseki != null) {
                    JosekiView(mainWindow, joseki)
                } else {
                    PreferencesView(mainWindow, Preferences.josekiPreferences)
                }
            })
            add(createTaskButton("Open SGF File", "open-file") { OpenFileTask(mainWindow) })
            add(createButton("Preferences", "preferences") { PreferencesView(mainWindow) })
        }
    }

    fun createTaskButton(label: String, style: String, factory: () -> Task): Button {
        return createButton(label, style) {
            val task = factory()
            PromptTaskView(task, mainWindow)
        }
    }
}
