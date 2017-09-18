/*
GoKo a Go Client
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.goko.preferences

import uk.co.nickthecoder.goko.GnuGoPlayer
import uk.co.nickthecoder.goko.LocalPlayer
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.model.Game
import uk.co.nickthecoder.goko.model.GameVariationType
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

    val computerLevelP = IntParameter("computerLevel", minValue = 1, maxValue = 20, value = 10)

    init {
        taskD.addParameters(boardSizeP, computerPlaysP, computerLevelP, handicapP,
                fixedHandicapPointsP, komiP, timeLimitP, allowUndoP, gameVariationP)

        // Cannot play Hidden move etc against the computer.
        GameVariationType.values().filter { it.twoPlayerOnly }.forEach {
            gameVariationP.removeKey(it.name)
        }
    }

    override fun run() {
        Preferences.save()
    }

    override fun initialiseGame(game: Game) {
        val human = LocalPlayer(game, StoneColor.opposite(computerPlaysP.value!!), Preferences.yourName, Preferences.yourRank)
        human.timeRemaining = game.metaData.timeLimit.copyTimeLimit()

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
