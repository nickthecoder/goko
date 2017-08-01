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

enum class StoneColor {
    BLACK, WHITE, NONE, EDGE;

    companion object {
        fun opposite(color: StoneColor) = if (color == WHITE) BLACK else WHITE
    }

    fun isStone() = this == BLACK || this == WHITE

    fun opposite(): StoneColor {
        if (this == WHITE) return BLACK
        if (this == BLACK) return WHITE
        return this
    }
}
