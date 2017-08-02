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

import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.gui.PlayingView
import uk.co.nickthecoder.goko.model.Game
import uk.co.nickthecoder.goko.model.GameVariationType
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.*

abstract class AbstractGamePreferences : AbstractTask() {

    abstract val style: String

    val boardSizeP = ChoiceParameter("boardSize", value = 19)
            .choice("19", 19, "Standard (19x19)")
            .choice("17", 17, "(17x17)")
            .choice("15", 15, "(15x15)")
            .choice("13", 13, "Medium (13x13)")
            .choice("11", 11, "(11x11)")
            .choice("9", 9, "Small (9x9)")


    val handicapP = IntParameter("handicap", value = 0, range = 0..9)

    val fixedHandicapPointsP = BooleanParameter("fixedHandicapPoints", value = true)

    val komiP = DoubleParameter("komi", value = 0.0, minValue = -100.0, maxValue = 100.0)

    val timeLimitP = Preferences.timeLimitPreferences.createTimeLimitChoice()

    val allowUndoP = BooleanParameter("allowUndo", value = true)

    val gameVariationP = ChoiceParameter("gameVariation", value = GameVariationType.NORMAL).enumChoices()

    fun createView(mainWindow: MainWindow): PlayingView {

        val game = Game(size = boardSizeP.value!!)
        game.metaData.komi = komiP.value!!
        game.metaData.handicap = handicapP.value!!
        game.metaData.fixedHandicaptPoints = fixedHandicapPointsP.value!!
        game.metaData.timeLimit = timeLimitP.value!!

        val view = PlayingView(mainWindow, game, allowUndoP.value!!)
        view.boardView.colorVariation = gameVariationP.value!!

        changePlayers(game)

        game.metaData.enteredBy = "GoKo"
        game.start()
        return view
    }

    abstract fun changePlayers(game: Game)

    abstract fun createLaunchTask(mainWindow: MainWindow): Task
}
