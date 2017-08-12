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
import uk.co.nickthecoder.goko.preferences.Preferences
import uk.co.nickthecoder.paratask.util.runAfterDelay

/**
 * Automatically play the opponents moves when solving Go problems
 */
class ProblemOpponent(val game: Game, override val color: StoneColor) : Player {

    override var label = "Opponent"

    override var timeRemaining: TimeLimit = NoTimeLimit()

    override val rank = ""

    override fun yourTurn() {
        if (Preferences.problemsAutomaticOpponent == true) {
            val currentNode = game.currentNode
            val nextNode = currentNode.children.firstOrNull()

            nextNode?.let {
                runAfterDelay(500) {
                    game.apply(it)
                    GoKo.stoneSound()
                }
            }
        }
    }

    override fun makeMove(point: Point, onMainLine: Boolean) {
        game.variation.makeMove(point, color, onMainLine)
    }

    override fun pass(onMainLine: Boolean) {
        game.variation.makeMove(null, color, onMainLine)
    }

    override fun canClickToPlay(): Boolean {
        if (game.currentNode.children.isEmpty()) {
            return true
        }
        return false
    }
}
