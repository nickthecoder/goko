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

import uk.co.nickthecoder.goko.model.*

class EditGamePlayer(val game: Game, override val color: StoneColor, override val rank: String = "")
    : Player {

    override var timeRemaining: TimeLimit = NoTimeLimit()

    override var label = color.toString().toLowerCase().capitalize()

    override fun makeMove(point: Point) {
        game.move(point, color, onMainLine = false)
    }

    override fun pass() {
        game.pass()
    }

    override fun canClickToPlay() = true
}
