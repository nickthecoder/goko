package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.Player

/**
 * Note, no attempt has been made to prevent concurrent modifications to Game's listeners, so create all of the game
 * listeners at the beginning of the game, and do not remove them.
 */
interface GameListener {

    fun gameEnded(winner: Player?) {}

    fun moved() {}

    fun addedMark(mark: Mark) {}

    fun removedMark(mark: Mark) {}

    fun stoneChanged(point: Point) {}

    fun updatedCurrentNode() {}

    fun nodeChanged(node: GameNode) {}

    fun updatedMetaData() {}
}
