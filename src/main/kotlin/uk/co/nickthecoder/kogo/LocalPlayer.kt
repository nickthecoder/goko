package uk.co.nickthecoder.kogo

import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.Point
import uk.co.nickthecoder.kogo.model.StoneColor

open class LocalPlayer(val game: Game, override val color: StoneColor, val name: String = "Human", override val rank: String = "")
    : Player {

    override val label
        get() = name

    override fun makeMove(point: Point) {
        game.move(point, color, this)
    }

    override fun pass() {
        game.pass(this)
    }

    override fun canClickToPlay() = true
}
