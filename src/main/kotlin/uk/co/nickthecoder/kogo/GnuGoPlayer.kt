package uk.co.nickthecoder.kogo

import javafx.application.Platform
import uk.co.nickthecoder.kogo.model.*

/**
 * Interfaces with the GnuGo AI using gtp mode
 */
class GnuGoPlayer(val game: Game, override val color: StoneColor, level: Int = 10) : Player, GnuGoClient {

    override var label = "Gnu Go level ${level}"

    override val rank = ""

    override var timeRemaining: TimeLimit = NoTimeLimit.instance

    /**
     * When a move is generated, check with this to ensure that the game's current node hasn't changed between
     * requesting the move, and the move being picked.
     */
    private var expectedCurrentNode: GameNode? = null

    val gnuGo = GnuGo(game, level)

    fun start() {
        gnuGo.start()
    }

    override fun placeHandicap() {
        gnuGo.placeHandicap(this)
    }

    override fun yourTurn() {
        expectedCurrentNode = game.currentNode
        gnuGo.generateMove(color, this)
    }

    override fun makeMove(point: Point) {
        throw IllegalStateException("It's GnuGoPlayer's turn!")
    }

    override fun pass() {
        throw IllegalStateException("It's GnuGoPlayer's turn!")
    }

    override fun canClickToPlay() = false

    override fun generatedMove(point: Point) {
        Platform.runLater {
            if (game.currentNode === expectedCurrentNode) {
                game.move(point, color)
            } else {
                println("Generated move ignored, as the game's current node has changed.")
                gnuGo.syncBoard()
            }
        }
    }

    override fun generatedPass() {
        Platform.runLater {
            if (game.currentNode === expectedCurrentNode) {
                game.pass(true)
            } else {
                println("Generated move ignored, as the game's current node has changed.")
            }
        }
    }

    override fun generatedResign() {
        Platform.runLater {
            game.resign(this)
        }
    }
}
