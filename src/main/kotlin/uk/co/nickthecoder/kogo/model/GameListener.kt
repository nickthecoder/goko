package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.Player

interface GameListener {

    fun matchResult(game: Game, winner: Player?) {}

    fun moved() {}
}
