package uk.co.nickthecoder.goko.model

open class StandardGo(val game: Game) : GameVariation {

    override val allowHelp = true

    val board
        get() = game.board

    /**
     * The number of handicap stones the black player still has to play.
     * Is zero when using fixed handicap points (on the star points).
     */
    var freeHandicaps: Int = 0

    override fun start() {
        if (game.metaData.fixedHandicaptPoints) {
            placeFixedHandicap()
            game.playerToMove = game.players[if (game.metaData.handicap!! < 2) StoneColor.BLACK else StoneColor.WHITE]!!
            game.playerToMove.yourTurn()
            game.root.colorToPlay = game.playerToMove.color
        } else {
            game.playerToMove = game.players[StoneColor.BLACK]!!
            if (game.metaData.handicap!! > 2) {
                freeHandicaps = game.metaData.handicap!!
                game.playerToMove.placeHandicap()
            } else {
                game.playerToMove.yourTurn()
            }
        }

    }

    override fun canPlayAt(point: Point?): Boolean {
        if (point == null) {
            return freeHandicaps != 0 // Cannot pass while placing handicap stones
        }
        return game.canPlayAt(point)
    }

    /**
     * Make a move at point, or null for a pass.
     */
    override fun makeMove(point: Point?, color: StoneColor, onMainLine: Boolean) {

        if (freeHandicaps > 0) {
            if (game.currentNode != game.root) {
                throw IllegalStateException("Can only place handicap stones in the root node")
            }
            if (point == null) {
                throw IllegalStateException("Cannot pass while adding handicap stones")
            }
            freeHandicaps--
            game.root.addStone(board, point, color)
            game.nodeChanged(game.currentNode)

            if (freeHandicaps == 0) {
                game.root.colorToPlay = StoneColor.WHITE

                game.playerToMove = game.players[game.root.colorToPlay]!!
                game.playerToMove.yourTurn()
                game.listeners.forEach {
                    it.nodeChanged(game.root)
                }
            }
            return
        }

        if (point == null) {
            game.pass(color, onMainLine)
        } else {
            game.move(point, color, onMainLine)
        }

    }

    fun placeFixedHandicap() {
        println("Placing fixed handicap stones.")
        val start: Int
        if (board.size < 13) {
            start = 2
        } else {
            start = 3
        }
        val jump = (board.size - 1) / 2 - start

        for (i in 0..game.metaData.handicap!! - 1) {
            val h = handicapInfo[i]
            val point = Point(start + h.x * jump, start + h.y * jump)
            game.root.addStone(board, point, StoneColor.BLACK)
        }
    }


    companion object {

        val handicapInfo = listOf(
                Point(0, 0), Point(2, 2), Point(2, 0), Point(0, 2),
                Point(1, 1),
                Point(1, 0), Point(1, 2), Point(0, 1), Point(2, 1))

    }

    override fun displayColor(point: Point): StoneColor = board.getStoneAt(point)

}

class OneColorGo(game: Game) : StandardGo(game) {

    override val allowHelp = false

    override fun displayColor(point: Point): StoneColor {
        val stone = board.getStoneAt(point)
        if (stone == StoneColor.NONE) {
            return stone
        } else {
            return StoneColor.WHITE
        }
    }

}

class TwoColorOneColorGo(game: Game) : StandardGo(game) {

    override val allowHelp = false

    override fun displayColor(point: Point): StoneColor {

        val stone = board.getStoneAt(point)
        if (stone == StoneColor.NONE) {
            return stone
        } else {
            val foo = (point.y * board.size + point.x).hashCode() % 2
            return if (foo == 0) StoneColor.WHITE else StoneColor.BLACK
        }
    }
}
