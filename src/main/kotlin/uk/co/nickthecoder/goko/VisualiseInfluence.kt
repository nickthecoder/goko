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
import uk.co.nickthecoder.goko.gui.MarksView
import uk.co.nickthecoder.goko.gui.SymbolMarkView
import uk.co.nickthecoder.goko.model.*

class VisualiseInfluence(val game: Game, val color: StoneColor, val type: String, val marksView: MarksView) : GnuGoClient {

    val node = game.currentNode

    fun visualise() {
        // Run later to ensure that GnuGo has been updated first
        Platform.runLater {
            game.createGnuGo().influence(type, this, color)
        }
    }

    override fun influenceResults(results: List<List<Double>>) {
        if (game.currentNode === node) {
            Platform.runLater {
                val max: Double = results.map { it.max() ?: 0.0 }.max() ?: 0.0
                for (y in 0..game.board.size - 1) {
                    for (x in 0..game.board.size - 1) {
                        val point = Point(x, y)
                        val symbol = InfluenceMark(point)
                        val mv = SymbolMarkView(symbol)
                        marksView.add(mv, color = color)
                        var opacity = results[x][y] / max
                        if (color == StoneColor.BLACK) {
                            opacity = opacity * 0.7
                        }
                        mv.opacity = opacity
                    }
                }
            }
        }
    }
}
