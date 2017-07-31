package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.Player

/**
 * Note, no attempt has been made to prevent concurrent modifications to Game's listeners, so create all of the game
 * listeners at the beginning of the game, and do not remove them.
 */
interface GameListener {

    fun madeMove( gameNode : GameNode ) {}

    fun undoneMove( gameNode : GameNode ) {}

    fun addedMark(mark: Mark) {}

    /**
     * A point on the board has changed. This wil be called before madeMove when a MoveNode is applied.
     * It is also called for each stone captured.
     */
    fun stoneChanged(point: Point) {}

    fun removedMark(mark: Mark) {}


    fun updatedMetaData() {}

    fun gameEnded(winner: Player?) {}

    /**
     * Unlike all the other methods, this may refer to a node which is NOT the current game node.
     */
    fun nodeChanged(node: GameNode) {}

}
