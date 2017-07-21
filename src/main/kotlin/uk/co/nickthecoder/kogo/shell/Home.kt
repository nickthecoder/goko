package uk.co.nickthecoder.kogo.shell

import javafx.scene.control.Button
import uk.co.nickthecoder.kogo.gui.JosekiView
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.kogo.preferences.PreferencesView
import uk.co.nickthecoder.paratask.Task

class Home(mainWindow: MainWindow) : GridView(mainWindow) {

    override val title = "Home"

    override val viewStyle = "home"

    override fun addButtons() {
        with(buttons) {
            add(createButton("Quick Game", "quick-game", QuickGameTask(mainWindow)))
            add(createButton("Two Player Game", "two-player-game", TwoPlayerGameTask(mainWindow)))
            add(createButton("Challenge Match", "challenge-match", ChallengeMatchTask(mainWindow)))
            add(createButton("Open SGF File", "open-file", OpenFileTask(mainWindow)))
            add(createButton("Problems", "problems") { ProblemsView(mainWindow) })
            val joseki = Preferences.josekiDirectionary
            if (joseki != null) {
                add(createButton("Joseki Dictionary", "joseki") { JosekiView(mainWindow, joseki) })
            }
            add(createButton("Preferences", "preferences") { PreferencesView(mainWindow) })
        }
    }

    fun createButton(label: String, style: String, task: Task): Button {
        return createButton(label, style) { PromptTaskView(task, mainWindow) }
    }
}
