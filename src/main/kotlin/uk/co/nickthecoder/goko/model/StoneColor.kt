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

/**
 * isClickable is used to determine if the mouse pointer should show a mark at the point while moving over it.
 * In HiddenMoveGo, the hidden stones are clickable.
 */
enum class StoneColor(val isAStone: Boolean, val isLiberty: Boolean, val isClickable: Boolean = false) {

    BLACK(true, false),
    WHITE(true, false),
    NONE(false, true, true),
    EDGE(false, false),

    HIDDEN_WHITE(true, false, true),
    HIDDEN_BLACK(true, false, true),
    HIDDEN_BOTH(false, true, true);

    fun realColor(): StoneColor =
            when (this) {
                HIDDEN_BOTH -> NONE
                HIDDEN_WHITE -> WHITE
                HIDDEN_BLACK -> BLACK
                else -> this
            }

    // TODO Remove this function, and rename isAStone to isStone
    fun isStone() = isAStone

    fun opposite(): StoneColor {
        if (this == WHITE) return BLACK
        if (this == BLACK) return WHITE
        return this
    }

    fun humanString() : String = toString().toLowerCase().capitalize()

    companion object {
        fun opposite(color: StoneColor) = if (color == WHITE) BLACK else WHITE
    }
}
