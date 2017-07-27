package uk.co.nickthecoder.kogo

import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.Point
import uk.co.nickthecoder.kogo.model.StoneColor

open class LocalPlayer(val game: Game, override val color: StoneColor, name: String = "Human", override val rank: String = "")
    : Player {

    override var timeRemaining = game.metaData.timeLimit.copy()

    override var label = name

    override fun makeMove(point: Point) {
        game.move(point, color)
    }

    override fun pass() {
        game.pass()
    }

    override fun canClickToPlay() = true
}
