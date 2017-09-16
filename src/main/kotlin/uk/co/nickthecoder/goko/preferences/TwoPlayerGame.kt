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

import uk.co.nickthecoder.goko.LocalPlayer
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.model.*
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter

open class TwoPlayerGame : AbstractGamePreferences(), GameListener {

    override val style = "two-player"

    final override val taskD = TaskDescription("twoPlayerGame")

    val blackPlayerP = StringParameter("blackName", label = "Black's Name", required = false, value = Preferences.yourName)

    val whitePlayerP = StringParameter("whiteName", label = "Whites's Name", required = false, value = "Mister White")

    val hiddenMovesBlackP = IntParameter("hiddenMovesBlack", value = 3, minValue = 1, maxValue = 30)
    val hiddenMovesWhiteP = IntParameter("hiddenMovesWhite", value = 3, minValue = 1, maxValue = 30)

    init {
        taskD.addParameters(boardSizeP, blackPlayerP, whitePlayerP, handicapP,
                fixedHandicapPointsP, komiP, timeLimitP, allowUndoP, gameVariationP, hiddenMovesBlackP, hiddenMovesWhiteP)

        hiddenMovesBlackP.hidden = true
        hiddenMovesWhiteP.hidden = true

        gameVariationP.listen {
            val isHiddenMoveGo = gameVariationP.value == GameVariationType.HIDDEN_MOVE_GO
            hiddenMovesBlackP.hidden = !isHiddenMoveGo
            hiddenMovesWhiteP.hidden = !isHiddenMoveGo

            handicapP.hidden = isHiddenMoveGo
            fixedHandicapPointsP.hidden = isHiddenMoveGo
        }
    }

    override fun run() {
        Preferences.save()
    }

    override fun initialiseGame(game: Game) {

        val blackPlayer = LocalPlayer(game, StoneColor.BLACK, blackPlayerP.value)
        val whitePlayer = LocalPlayer(game, StoneColor.WHITE, whitePlayerP.value)

        blackPlayer.timeRemaining = game.metaData.timeLimit.copy()
        whitePlayer.timeRemaining = game.metaData.timeLimit.copy()

        game.addPlayer(blackPlayer)
        game.addPlayer(whitePlayer)
        if (gameVariationP.value == GameVariationType.HIDDEN_MOVE_GO) {
        }

        game.file = Preferences.gameFile("Two Player")
    }

    override fun createGameVariation(game: Game): GameVariation {
        if (gameVariationP.value == GameVariationType.HIDDEN_MOVE_GO) {
            return HiddenMoveGo(game,
                    hiddenMoveCountBlack = hiddenMovesBlackP.value!!,
                    hiddenMoveCountWhite = hiddenMovesWhiteP.value!!)
        }
        return super.createGameVariation(game)
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
