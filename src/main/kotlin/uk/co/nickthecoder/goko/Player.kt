package uk.co.nickthecoder.goko

import uk.co.nickthecoder.goko.model.Point
import uk.co.nickthecoder.goko.model.StoneColor
import uk.co.nickthecoder.goko.model.TimeLimit

interface Player {

    var label: String

    val rank: String

    var timeRemaining: TimeLimit

    val color: StoneColor

    val letter: String
        get() = if (color == StoneColor.BLACK) "B" else if (color == StoneColor.WHITE) "W" else "?"

    fun tidyUp() {}

    fun placeHandicap() {}

    fun yourTurn() {}

    fun canClickToPlay(): Boolean

    fun makeMove(point: Point)

    fun pass()
}
