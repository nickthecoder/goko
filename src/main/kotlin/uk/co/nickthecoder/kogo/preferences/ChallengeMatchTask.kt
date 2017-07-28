package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.preferences.ChallengeMatchPreferences
import uk.co.nickthecoder.kogo.preferences.Preferences

class ChallengeMatchTask(val mainWindow: MainWindow) : ChallengeMatchPreferences() {

    init {
        taskD.copyValuesFrom(Preferences.challengeMatchPreferences.taskD)
    }

    override fun run() {
        mainWindow.changeView(createView(mainWindow))
    }

}
