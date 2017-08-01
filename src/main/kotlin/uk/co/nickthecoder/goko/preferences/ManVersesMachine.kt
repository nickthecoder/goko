package uk.co.nickthecoder.goko.preferences

import uk.co.nickthecoder.goko.GnuGoPlayer
import uk.co.nickthecoder.goko.LocalPlayer
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.model.Game
import uk.co.nickthecoder.goko.model.StoneColor
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter

open class ManVersesMachine : AbstractGamePreferences() {

    override val style = "man-vs-machine"

    final override val taskD = TaskDescription("manVersesMachine", description = "Play a one-off game against the Gnu Go robot.")

    val computerPlaysP = ChoiceParameter("computerPlays", value = StoneColor.BLACK)
            .choice("BLACK", StoneColor.BLACK, "Black")
            .choice("WHITE", StoneColor.WHITE, " White")

    val computerLevelP = IntParameter("computerLevel", range = 1..20, value = 10)

    init {
        taskD.addParameters( boardSizeP, computerPlaysP, computerLevelP, handicapP,
                fixedHandicapPointsP, komiP, timeLimitP, allowUndoP, gameVariationP )
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

    override fun createLaunchTask(mainWindow: MainWindow): Task {
        return ManVersesMachineLauncher(mainWindow, this)
    }
}

class ManVersesMachineLauncher(val mainWindow: MainWindow, parent: Task) : ManVersesMachine() {

    init {
        taskD.copyValuesFrom(parent.taskD)
    }

    override fun run() {
        mainWindow.changeView(createView(mainWindow))
    }

}
