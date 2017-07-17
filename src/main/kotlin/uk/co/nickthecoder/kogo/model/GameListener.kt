package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.Player

interface GameListener {

    fun matchResult(game: Game, winner: Player?) {}

    fun moved() {}

    fun addedMark( mark: Mark ) {}

    fun removedMark( mark: Mark ) {}

    fun stoneChanged(point: Point, byPlayer: Player?) {}
}
