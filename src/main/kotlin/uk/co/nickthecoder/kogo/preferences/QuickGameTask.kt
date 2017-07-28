package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.kogo.GnuGoPlayer
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.gui.PlayingView
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.kogo.preferences.QuickGamePreferences
import java.io.File

class QuickGameTask(val mainWindow: MainWindow) : QuickGamePreferences() {

    init {
        taskD.copyValuesFrom(Preferences.quickGamePreferences.taskD)
    }

    override fun run() {
        mainWindow.changeView(createView(mainWindow))
    }

}
