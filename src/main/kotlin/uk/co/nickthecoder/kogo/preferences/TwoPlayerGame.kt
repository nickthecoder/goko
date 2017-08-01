package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.GameListener
import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.StringParameter

open class TwoPlayerGame : AbstractGamePreferences(), GameListener {

    override val style = "two-player"

    final override val taskD = TaskDescription("twoPlayerGame")

    val blackPlayerP = StringParameter("blackName", label = "Black's Name", required = false, value = Preferences.yourName)

    val whitePlayerP = StringParameter("whiteName", label = "Whites's Name", required = false, value = "Mister White")

    init {
        taskD.addParameters(boardSizeP, blackPlayerP, whitePlayerP, handicapP,
                fixedHandicapPointsP, komiP, timeLimitP, allowUndoP, gameVariationP)
    }

    override fun run() {
        Preferences.save()
    }

    override fun changePlayers(game: Game) {

        val blackPlayer = LocalPlayer(game, StoneColor.BLACK, blackPlayerP.value)
        val whitePlayer = LocalPlayer(game, StoneColor.WHITE, whitePlayerP.value)

        blackPlayer.timeRemaining = game.metaData.timeLimit.copy()
        whitePlayer.timeRemaining = game.metaData.timeLimit.copy()

        game.addPlayer(blackPlayer)
        game.addPlayer(whitePlayer)

        game.file = Preferences.gameFile("Two Player")
    }


    override fun createLaunchTask(mainWindow: MainWindow): Task {
        return TwoPlayerGameLauncher(mainWindow, this)
    }

}


class TwoPlayerGameLauncher(val mainWindow: MainWindow, parent: Task) : TwoPlayerGame() {

    init {
        taskD.copyValuesFrom(parent.taskD)
    }

    override fun run() {
        mainWindow.changeView(createView(mainWindow))
    }

}
