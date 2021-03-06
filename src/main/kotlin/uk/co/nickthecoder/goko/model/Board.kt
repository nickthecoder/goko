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

class Board(val size: Int, val game: Game) {

    private val points = List(size, { MutableList(size, { StoneColor.NONE }) })

    /**
     * A copy of the board is made when testing if a move would be self atari
     * The copy shares the same Game, but should NOT fire events when stones are placed.
     */
    var isCopy = false

    fun contains(x: Int, y: Int) = x >= 0 && y >= 0 && x < size && y < size

    fun contains(point: Point) = point.x >= 0 && point.y >= 0 && point.x < size && point.y < size

    fun getStoneAt(x: Int, y: Int): StoneColor {
        if (contains(x, y)) {
            return points[x][y]
        } else {
            return StoneColor.EDGE
        }
    }

    fun getStoneAt(point: Point): StoneColor {
        if (contains(point)) {
            return points[point.x][point.y]
        } else {
            return StoneColor.EDGE
        }
    }

    fun setStoneAt(point: Point, color: StoneColor) {
        if (contains(point)) {
            points[point.x][point.y] = color
            if (!isCopy) {
                game.listeners.forEach { listener ->
                    listener.stoneChanged(point)
                }
            }
        }
    }

    fun removeStoneAt(point: Point) {
        points[point.x][point.y] = StoneColor.NONE
        if (!isCopy) {
            game.listeners.forEach { listener ->
                listener.stoneChanged(point)
            }
        }
    }

    fun checkLiberties(point: Point): Set<Point>? {

        val color = getStoneAt(point).realColor()
        val opposite = color.opposite()
        val group = mutableSetOf<Point>()

        fun surrounded(innerPoint: Point): Boolean {
            val c = getStoneAt(innerPoint).realColor()
            if (c == StoneColor.NONE) {
                return false
            }
            if (c == StoneColor.EDGE || c == opposite) {
                return true
            }
            if (group.contains(innerPoint)) {
                return true
            }
            group.add(innerPoint)

            return surrounded(Point(innerPoint.x - 1, innerPoint.y)) &&
                    surrounded(Point(innerPoint.x + 1, innerPoint.y)) &&
                    surrounded(Point(innerPoint.x, innerPoint.y - 1)) &&
                    surrounded(Point(innerPoint.x, innerPoint.y + 1))
        }

        if (surrounded(point)) {
            return group
        } else {
            return null
        }
    }

    fun removeTakenStones(point: Point): Set<Point> {

        fun removeTakenStonesQuarter(innerPoint: Point): Set<Point> {
            if (contains(innerPoint) && getStoneAt(innerPoint).realColor() != getStoneAt(point).realColor()) {
                val group = checkLiberties(innerPoint)
                group?.forEach {
                    removeStoneAt(it)
                }
                return group ?: setOf<Point>()
            } else {
                return setOf()
            }
        }

        val removedPoints = mutableSetOf<Point>()
        removedPoints += removeTakenStonesQuarter(Point(point.x - 1, point.y))
        removedPoints += removeTakenStonesQuarter(Point(point.x + 1, point.y))
        removedPoints += removeTakenStonesQuarter(Point(point.x, point.y - 1))
        removedPoints += removeTakenStonesQuarter(Point(point.x, point.y + 1))

        return removedPoints
    }

    fun copy(): Board {
        val result = Board(size, game)
        result.isCopy = true
        for (x in 0..size - 1) {
            for (y in 0..size - 1) {
                result.points[x][y] = points[x][y]
            }
        }
        return result
    }

    /**
     * The hash is used to detect kos. If the stones on the board are the same as a previous position, and
     * the player to move is the same, then we have a repeated board position, which is illegal Ko move.
     * Note that we use the REAL COLOR of each point, so that the hash of HIDDEN_WHITE is the same as WHITE.
     * This allows kos in HiddenMoveGo to work correctly.
     */
    override fun hashCode(): Int {
        return points.map { it.map { it.realColor() } }.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is Board) {
            return other.points == this.points
        } else {
            return false
        }
    }
}
