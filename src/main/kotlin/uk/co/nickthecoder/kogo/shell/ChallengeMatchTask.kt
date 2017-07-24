package uk.co.nickthecoder.kogo.shell

import uk.co.nickthecoder.kogo.GnuGoPlayer
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.gui.PlayingView
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.kogo.preferences.ChallengeMatchPreferences
import uk.co.nickthecoder.kogo.preferences.Preferences

class ChallengeMatchTask(val mainWindow: MainWindow) : ChallengeMatchPreferences() {

    init {
        taskD.copyValuesFrom(Preferences.challengeMatchPreferences.taskD)
    }

    override fun run() {

        Preferences.challengeMatchPreferences.taskD.copyValuesFrom(taskD)

        val game = Game(size = boardSizeP.value!!)
        val view = PlayingView(mainWindow, game)

        val human = LocalPlayer(game, StoneColor.opposite(computerPlaysP.value!!), Preferences.yourName, Preferences.yourRank)

        val gnuGo = GnuGoPlayer(game, computerPlaysP.value!!)
        gnuGo.start()

        game.addPlayer(gnuGo)
        game.addPlayer(human)

        game.placeHandicap(handicapP.value!!)

        game.gameListeners.add(Preferences.challengeMatchPreferences)
        mainWindow.changeView(view)
        game.start()

    }
}
