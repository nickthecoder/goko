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

        game.metaData.handicap = handicapP.value!!
        game.metaData.komi = if (game.metaData.handicap == 0) komiP.value!! else 0.0
        game.metaData.japaneseRules = rulesP.value!!
        game.metaData.timeLimit = timeLimitP.value!!

        val human = LocalPlayer(game, StoneColor.opposite(computerPlaysP.value!!), Preferences.yourName, Preferences.yourRank)
        human.timeRemaining = game.metaData.timeLimit

        val gnuGo = GnuGoPlayer(game, computerPlaysP.value!!)
        gnuGo.start()

        game.addPlayer(gnuGo)
        game.addPlayer(human)

        game.listeners.add(Preferences.challengeMatchPreferences)
        mainWindow.changeView(view)
        game.start()

    }
}
