package uk.co.nickthecoder.kogo

import javafx.application.Platform
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.paratask.util.process.BufferedSink
import uk.co.nickthecoder.paratask.util.process.Exec
import java.io.OutputStreamWriter
import java.io.Writer

/**
 * Interfaces with the GnuGo AI using gtp mode
 */
class GnuGoPlayer(val game: Game, override val color: StoneColor, level: Int = 10) : Player, GnuGoListener {

    override val label = "Gnu Go level ${level}"

    override val rank = ""

    override var timeRemaining: TimeLimit = NoTimeLimit.instance

    val gnuGo = GnuGo(game, level)

    fun start() {
        gnuGo.start()
    }

    override fun yourTurn() {
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
            game.move(point, color, this)
        }
    }

    override fun generatedPass() {
        Platform.runLater {
            game.pass(this)
        }
    }

    override fun generatedResign() {
        Platform.runLater {
            game.resign(this)
        }
    }
}
