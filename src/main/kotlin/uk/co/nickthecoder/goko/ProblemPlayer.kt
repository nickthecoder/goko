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

import uk.co.nickthecoder.goko.model.Game
import uk.co.nickthecoder.goko.model.Point
import uk.co.nickthecoder.goko.model.StoneColor

class ProblemPlayer(game: Game, color: StoneColor) : LocalPlayer(game, color) {

    val offPisteComment = """Hmm, that move is not not part of the solution.

Maybe the Go Problem doesn't contain solutions.

Maybe you've found an alternate solution,

Or maybe you just got it wrong ;-)
"""
    override fun makeMove(point: Point, onMainLine: Boolean) {
        val node = game.currentNode
        val count = node.children.size
        super.makeMove(point, onMainLine)
        if (node.children.size != count) {
            game.currentNode.comment = offPisteComment
            game.nodeDataChanged()
        }
    }

    override fun pass(onMainLine: Boolean) {
        val node = game.currentNode
        val count = node.children.size
        super.pass(onMainLine)
        if (node.children.size != count) {
            game.currentNode.comment = offPisteComment
            game.nodeDataChanged()
        }
    }

    override fun canClickToPlay() = true
}
