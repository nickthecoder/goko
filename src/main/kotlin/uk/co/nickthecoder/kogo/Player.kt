package uk.co.nickthecoder.kogo

import uk.co.nickthecoder.kogo.model.Point
import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.kogo.model.TimeLimit

interface Player {

    val label: String

    val rank: String

    var timeRemaining: TimeLimit

    val color: StoneColor

    val letter: String
        get() = if (color == StoneColor.BLACK) "B" else if (color == StoneColor.WHITE) "W" else "?"

    fun tidyUp() {}

    fun yourTurn() {}

    fun canClickToPlay(): Boolean

    fun makeMove(point: Point)

    fun pass()
}
