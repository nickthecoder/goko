package uk.co.nickthecoder.kogo.shell

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

        val game = Game(size = boardSizeP.value!!)
        game.metaData.komi = komiP.value!!
        game.metaData.handicap = handicapP.value!!
        game.metaData.fixedHandicaptPoints = fixedHandicapPointsP.value!!
        game.metaData.japaneseRules = rulesP.value!!
        game.metaData.timeLimit = timeLimitP.value!!

        val view = PlayingView(mainWindow, game)

        val human = LocalPlayer(game, StoneColor.opposite(computerPlaysP.value!!), Preferences.yourName, Preferences.yourRank)
        human.timeRemaining = game.metaData.timeLimit.copy()

        val gnuGo = GnuGoPlayer(game, computerPlaysP.value!!)
        gnuGo.start()

        game.addPlayer(gnuGo)
        game.addPlayer(human)

        game.file = Preferences.gameFile("Quick")

        mainWindow.changeView(view)
        game.start()
    }

}
