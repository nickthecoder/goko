package uk.co.nickthecoder.kogo.shell

import javafx.scene.control.Button
import uk.co.nickthecoder.kogo.gui.JosekiView
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.preferences.*
import uk.co.nickthecoder.paratask.Task

class Home(mainWindow: MainWindow) : GridView(mainWindow) {

    override val title = "Home"

    override val viewStyle = "home"

    override fun addButtons() {

        with(buttons) {
            add(createTaskButton("Quick Game", "quick-game") { QuickGameTask(mainWindow) })
            add(createTaskButton("Two Player Game", "two-player-game") { TwoPlayerGameTask(mainWindow) })
            add(createTaskButton("Challenge Match", "challenge-match") { ChallengeMatchTask(mainWindow) })
            add(createTaskButton("Open SGF File", "open-file") { OpenFileTask(mainWindow) })
            add(createButton("Problems", "problems") { ProblemsView(mainWindow) })
            add(createButton("Joseki Dictionary", "joseki") {
                val joseki = Preferences.josekiDirectionary
                if (joseki != null) {
                    JosekiView(mainWindow, joseki)
                } else {
                    PreferencesView(mainWindow, Preferences.josekiPreferences)
                }
            })
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
