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

data class Point(var x: Int, var y: Int) {

    override fun toString(): String {
        return xLabels[x] + (y + 1).toString()
    }

    fun isTouching(other: Point): Boolean {
        var dx = x - other.x
        var dy = y - other.y
        if (dx == -1) {
            dx = 1
        }
        if (dy == -1) {
            dy = 1
        }
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1)
    }

    companion object {
        val xLabels = listOf("A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T")

        fun labelX(x: Int) = if (x in 0..18) xLabels[x] else (x + 1).toString()

        fun labelY(y: Int) = (y + 1).toString()

        fun fromString(str: String): Point {
            val x = xLabels.indexOf(str.substring(0, 1))
            val y = Integer.parseInt(str.substring(1)) - 1
            return Point(x, y)
        }
    }

}
