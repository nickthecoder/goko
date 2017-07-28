package uk.co.nickthecoder.kogo.shell

import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.kogo.preferences.TwoPlayerGamePreferences

class TwoPlayerGameTask(val mainWindow: MainWindow) : TwoPlayerGamePreferences() {

    init {
        taskD.copyValuesFrom(Preferences.twoPlayerGamePreferences.taskD)
    }
    
    override fun run() {
        mainWindow.changeView(createView(mainWindow))
    }

}
