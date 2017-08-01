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
package uk.co.nickthecoder.goko

import javafx.application.Platform
import uk.co.nickthecoder.goko.model.AlternateMark
import uk.co.nickthecoder.goko.model.Game
import uk.co.nickthecoder.goko.model.MainLineMark
import uk.co.nickthecoder.goko.model.Point

/**
 */
class HintGenerator(val game: Game) : GnuGoClient {

    val node = game.currentNode

    fun hint() {
        game.createGnuGo().topMoves(node.colorToPlay, this)
    }

    override fun topMoves(points: List<Pair<Point, Double>>) {
        if (game.currentNode === node) {
            Platform.runLater {
                points.forEach { (point, probability) ->
                    // I think there's a bug in my version of gnugo - it always returns the last played move as
                    // the first element in the list.
                    if (!game.board.getStoneAt(point).isStone()) {
                        if (probability > 25.0) {
                            game.addMark(MainLineMark(point))
                        } else if (probability > 10.0) {
                            game.addMark(AlternateMark(point))
                        }
                    }
                }
            }
        }
    }
}
