package uk.co.nickthecoder.kogo

import uk.co.nickthecoder.kogo.model.StoneColor

interface Player {

    val label: String

    val rank: String

    val color: StoneColor

    val letter: String
        get() = if (color == StoneColor.BLACK) "B" else if (color == StoneColor.WHITE) "W" else "?"

    fun tidyUp() {}

    fun yourTurn() {}
}
