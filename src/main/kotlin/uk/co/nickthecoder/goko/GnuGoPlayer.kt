/*
GoKo a Go Client
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.goko

import javafx.application.Platform
import uk.co.nickthecoder.goko.model.*

/**
 * Interfaces with the GnuGo AI using gtp mode
 */
class GnuGoPlayer(val game: Game, override val color: StoneColor, level: Int = 10) : Player, GnuGoClient {

    override var label = "Gnu Go level $level"

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
                GoKo.stoneSound()
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
