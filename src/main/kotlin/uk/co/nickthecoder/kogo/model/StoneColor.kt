package uk.co.nickthecoder.kogo.model

enum class StoneColor {
    BLACK, WHITE, NONE, EDGE;

    companion object {
        fun opposite(color: StoneColor) = if (color == WHITE) BLACK else WHITE
    }

    fun isStone() = this == BLACK || this == WHITE
}
