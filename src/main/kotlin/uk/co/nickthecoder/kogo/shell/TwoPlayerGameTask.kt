package uk.co.nickthecoder.kogo.shell

import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.gui.PlayingView
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter

class TwoPlayerGameTask(val mainWindow: MainWindow) : AbstractTask() {

    override val taskD = TaskDescription("quickGame")

    val boardSizeP = ChoiceParameter<Int>("boardSize", value = 19)
            .choice("19", 19, "Standard (19x19)")
            .choice("13", 13, "Medium (13x13)")
            .choice("9", 9, "Small (9x9)")

    val blackPlayerP = StringParameter("blackName", label = "Black's Name", required = false, value = Preferences.yourName)

    val whitePlayerP = StringParameter("whiteName", label = "Whites's Name", required = false, value = "Mister White")

    val handicapP = IntParameter("handicap", value = 0, range = 0..9)

    init {
        taskD.addParameters(boardSizeP, blackPlayerP, whitePlayerP, handicapP)
    }

    override fun run() {

        val game = Game(sizeX = boardSizeP.value!!, sizeY = boardSizeP.value!!)
        val view = PlayingView(mainWindow, game)
        game.placeHandicap(handicapP.value!!)

        val blackPlayer = LocalPlayer(StoneColor.BLACK, blackPlayerP.value)
        val whitePlayer = LocalPlayer(StoneColor.WHITE, whitePlayerP.value)

        game.addPlayer(blackPlayer)
        game.addPlayer(whitePlayer)

        mainWindow.changeView(view)
        game.start()
    }

}
