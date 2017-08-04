package uk.co.nickthecoder.goko.model

interface GameVariation {

    /**
     * Can you use the Hints, Score and Hotspot features.
     */
    val allowHelp : Boolean

    fun start()

    /**
     * Can the player play at this point (or null for a Pass?
     */
    fun canPlayAt(point: Point?): Boolean

    /**
     * Make a move at point, or null for a pass.
     * Optional message is returned
     */
    fun makeMove(point: Point?, color: StoneColor, onMainLine: Boolean = true)

    fun capturedStones(colorCaptured: StoneColor, points: Set<Point>) {}

    fun displayColor(point: Point): StoneColor

}
