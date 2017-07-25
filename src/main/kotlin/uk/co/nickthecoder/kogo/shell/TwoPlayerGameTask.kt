package uk.co.nickthecoder.kogo.shell

import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.gui.PlayingView
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.kogo.preferences.TwoPlayerGamePreferences

class TwoPlayerGameTask(val mainWindow: MainWindow) : TwoPlayerGamePreferences() {

    init {
        taskD.copyValuesFrom(Preferences.twoPlayerGamePreferences.taskD)
    }

    override fun run() {

        val game = Game(size = boardSizeP.value!!)
        val view = PlayingView(mainWindow, game)

        game.metaData.handicap = handicapP.value!!
        game.metaData.komi = komiP.value!!
        game.metaData.japaneseRules = rulesP.value!!
        game.metaData.timeLimit = timeLimitP.value!!

        val blackPlayer = LocalPlayer(game, StoneColor.BLACK, blackPlayerP.value)
        val whitePlayer = LocalPlayer(game, StoneColor.WHITE, whitePlayerP.value)

        blackPlayer.timeRemaining = game.metaData.timeLimit.copy()
        whitePlayer.timeRemaining = game.metaData.timeLimit.copy()

        game.addPlayer(blackPlayer)
        game.addPlayer(whitePlayer)

        mainWindow.changeView(view)
        game.start()
    }
}
