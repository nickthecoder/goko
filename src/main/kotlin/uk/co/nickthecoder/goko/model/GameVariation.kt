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

interface GameVariation {

    /**
     * Can you use the Hints, Score and Hotspot features.
     */
    val allowHelp : Boolean

    fun start()

    /**
     * Can the player play at this point (or null for a Pass?
     */
    fun canPlayAt(point: Point?): Boolean

    /**
     * Make a move at point, or null for a pass.
     * Optional message is returned
     */
    fun makeMove(point: Point?, color: StoneColor, onMainLine: Boolean = true)

    fun capturedStones(colorCaptured: StoneColor, points: Set<Point>) {}

    fun displayColor(point: Point): StoneColor

}
