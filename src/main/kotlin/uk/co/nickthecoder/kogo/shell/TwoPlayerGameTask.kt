package uk.co.nickthecoder.kogo.shell

import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.gui.PlayingView
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.kogo.preferences.TwoPlayerGamePreferences
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter

class TwoPlayerGameTask(val mainWindow: MainWindow) : TwoPlayerGamePreferences() {

    init {
        taskD.copyValuesFrom(Preferences.twoPlayerGamePreferences.taskD)
    }

    override fun run() {

        val game = Game(size = boardSizeP.value!!)
        val view = PlayingView(mainWindow, game)
        game.placeHandicap(handicapP.value!!)

        val blackPlayer = LocalPlayer(game, StoneColor.BLACK, blackPlayerP.value)
        val whitePlayer = LocalPlayer(game, StoneColor.WHITE, whitePlayerP.value)

        game.addPlayer(blackPlayer)
        game.addPlayer(whitePlayer)

        mainWindow.changeView(view)
        game.start()
    }
}
