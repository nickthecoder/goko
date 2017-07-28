package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.kogo.GnuGoPlayer
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter

open class QuickGamePreferences : AbstractGamePreferences() {

    final override val taskD = TaskDescription("quickGame", description = "Play a one-off game against the Gnu Go robot.")

    val computerPlaysP = ChoiceParameter("computerPlays", value = StoneColor.BLACK)
            .choice("BLACK", StoneColor.BLACK, "Black")
            .choice("WHITE", StoneColor.WHITE, " White")

    val computerLevelP = IntParameter("computerLevel", range = 1..20, value = 10)

    init {
        taskD.addParameters(boardSizeP, computerPlaysP, computerLevelP, handicapP,
                fixedHandicapPointsP, komiP, timeLimitP, rulesP, colorVariationP)
    }

    override fun run() {
        Preferences.save()
    }

    override fun changePlayers(game: Game) {
        val human = LocalPlayer(game, StoneColor.opposite(computerPlaysP.value!!), Preferences.yourName, Preferences.yourRank)
        human.timeRemaining = game.metaData.timeLimit.copy()

        val gnuGo = GnuGoPlayer(game, computerPlaysP.value!!)
        gnuGo.start()

        game.addPlayer(gnuGo)
        game.addPlayer(human)

        game.file = Preferences.gameFile("Quick")
    }

    override fun createLauchTask(mainWindow: MainWindow): Task {
        return QuickGameTask(mainWindow, this)
    }
}

class QuickGameTask(val mainWindow: MainWindow, parent: Task) : QuickGamePreferences() {

    init {
        taskD.copyValuesFrom(parent.taskD)
    }

    override fun run() {
        mainWindow.changeView(createView(mainWindow))
    }

}
