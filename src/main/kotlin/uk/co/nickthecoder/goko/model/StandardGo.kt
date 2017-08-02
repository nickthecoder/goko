package uk.co.nickthecoder.goko.model

open class StandardGo(val game: Game) : GameVariation {

    val board
        get() = game.board

    override fun canPlayAt(point: Point?): Boolean {
        if (point == null) {
            return true // Can always pass
        }
        return game.canPlayAt(point)
    }

    /**
     * Make a move at point, or null for a pass.
     */
    override fun playAt(point: Point?, onMainLine: Boolean): String? {
        if (point == null) {
            game.pass(onMainLine)
        } else {
            game.move(point, game.playerToMove.color, onMainLine)
        }
        return null
    }

    /**
     * Special instruction to help the player make her move, or null if this is a regular move
     */
    override fun moveMessage(): String? = null

    /**
     * The special stone at point was used to capture a group.
     * Specifically here so that HiddenMoveGo can turn a hidden stone into a real one.
     */
    override fun usedToCapture(point: Point) {
        // Do nothing
    }
}
