package uk.co.nickthecoder.goko.model

interface GameVariation {

    /**
     * Can the player play at this point (or null for a Pass?
     */
    fun canPlayAt(point: Point?): Boolean

    /**
     * Make a move at point, or null for a pass.
     * Optional message is returned
     */
    fun playAt(point: Point?, onMainLine: Boolean = true): String?

    /**
     * Special instruction to help the player make her move, or null if this is a regular move
     */
    fun moveMessage(): String? = null

    /**
     * The special stone at point was used to capture a group.
     * Specifically here so that HiddenMoveGo can turn a hidden stone into a real one.
     */
    fun usedToCapture(point: Point)

}
