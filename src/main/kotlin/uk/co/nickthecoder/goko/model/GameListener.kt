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
package uk.co.nickthecoder.goko.model

import uk.co.nickthecoder.goko.Player

/**
 * Note, no attempt has been made to prevent concurrent modifications to Game's listeners, so create all of the game
 * listeners at the beginning of the game, and do not remove them.
 */
interface GameListener {

    fun madeMove(gameNode: GameNode) {}

    /**
     * A point on the board has changed. This wil be called before madeMove when a MoveNode is applied.
     * It is also called for each stone captured.
     */
    fun stoneChanged(point: Point) {}

    fun undoneMove(gameNode: GameNode) {}

    fun addedMark(mark: Mark) {}

    fun removedMark(mark: Mark) {}

    fun updatedMetaData() {}

    fun gameEnded(winner: Player?) {}

    /**
     * Unlike all the other methods, this may refer to a node which is NOT the current game node.
     */
    fun nodeDataChanged() {}

    fun gameMessage(message: String) {}
}
