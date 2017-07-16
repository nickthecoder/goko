package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.Player

interface BoardListener {

    fun stoneChanged(point: Point, byPlayer: Player?)

    fun moved() {}
}
