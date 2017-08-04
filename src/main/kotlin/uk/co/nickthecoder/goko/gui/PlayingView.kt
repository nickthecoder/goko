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
package uk.co.nickthecoder.goko.gui

import javafx.scene.control.SplitPane
import uk.co.nickthecoder.goko.model.Game

open class PlayingView(mainWindow: MainWindow, game: Game, val allowUndo: Boolean = true) : AbstractGoView(mainWindow, game) {

    override val title = "Playing"

    protected val split = SplitPane()

    val gameInfoView = GameInfoView(game, true)

    override fun build() {
        super.build()
        boardView.build()
        gameInfoView.build()

        whole.center = split

        with(split) {
            items.addAll(boardView.node, gameInfoView.node)
            dividers[0].position = 0.7
        }

        toolBar.items.addAll(saveB, reviewB)
        if (game.variation.allowHelp) {
            toolBar.items.addAll(hintB, estimateScoreB, hotspotsB)
        }
        toolBar.items.addAll(resignB, passB)
        if (allowUndo) {
            toolBar.items.add(undoB)
        }
    }

    override fun tidyUp() {
        super.tidyUp()
        game.tidyUp()
        boardView.tidyUp()
        gameInfoView.tidyUp()
    }

    override fun gameMessage(message: String) {
        gameInfoView.message(message)
    }

}

