package uk.co.nickthecoder.goko.model

import uk.co.nickthecoder.goko.Player
import uk.co.nickthecoder.goko.gui.SymbolMarkView

class HiddenMoveGo(val game: Game, val hiddenMoveCountBlack: Int, val hiddenMoveCountWhite: Int) : GameVariation, GameListener {

    override val type = GameVariationType.HIDDEN_MOVE_GO

    enum class State { HIDDEN_BLACK, HIDDEN_WHITE, NORMAL }

    var state = State.HIDDEN_BLACK

    val board
        get() = game.board

    val hiddenBlackMoves = mutableSetOf<Point>()
    val hiddenWhiteMoves = mutableSetOf<Point>()

    override fun start() {
        game.playerToMove = game.players[StoneColor.BLACK]!!
        game.playerToMove.yourTurn()
        game.listeners.add(this)
    }

    override fun canPlayAt(point: Point?): Boolean {

        when (state) {
            State.HIDDEN_BLACK -> {
                if (point == null) {
                    return hiddenBlackMoves.size == hiddenMoveCountBlack
                }
                return true
            }
            State.HIDDEN_WHITE -> {
                if (point == null) {
                    return hiddenWhiteMoves.size == hiddenMoveCountWhite
                }
                return true
            }
            State.NORMAL -> {
                if (point == null) {
                    return true // Can always pass
                } else {
                    val color = board.getStoneAt(point)
                    if (color == StoneColor.HIDDEN_BLACK || color == StoneColor.HIDDEN_WHITE) {
                        reveal(point)
                        return false
                    }

                    val result = game.canPlayAt(point)

                    if (color == StoneColor.HIDDEN_BOTH) {
                        reveal(point)
                    }

                    return result
                }
            }
        }
    }

    /**
     * Make a move at point, or null for a pass.
     */
    override fun makeMove(point: Point?, color: StoneColor, onMainLine: Boolean): String? {

        if (state == State.NORMAL) {

            if (point == null) {
                game.pass(color, onMainLine)
            } else {
                game.move(point, color, onMainLine)
            }

        } else {

            if (point == null) {
                return endSetup()
            } else {
                val list = if (color == StoneColor.BLACK) hiddenBlackMoves else hiddenWhiteMoves

                if (game.getMarkAt(point) == null) {
                    list.add(point)
                    game.addMark(TerritoryMark(point, color))
                } else {
                    list.remove(point)
                    game.removeMark(point)
                }
            }

        }
        return null

    }

    private fun endSetup(): String? {

        if (state == State.HIDDEN_BLACK) {
            if (hiddenBlackMoves.size != hiddenMoveCountBlack) {
                return "You must play $hiddenMoveCountBlack before passing"
            }
            state = State.HIDDEN_WHITE
            game.playerToMove = game.players[StoneColor.WHITE]!!

        } else {
            if (hiddenWhiteMoves.size != hiddenMoveCountWhite) {
                return "You must play $hiddenMoveCountWhite before passing"
            }
            state = State.NORMAL
            game.playerToMove = game.players[StoneColor.BLACK]!!
        }

        game.clearMarks()

        if (state == State.NORMAL) {
            val node = game.root
            hiddenBlackMoves.forEach { point ->
                if (hiddenWhiteMoves.contains(point)) {
                    node.addStone(board, point, StoneColor.HIDDEN_BOTH)
                } else {
                    node.addStone(board, point, StoneColor.HIDDEN_BLACK)
                }
            }
            hiddenWhiteMoves.forEach { point ->
                if (!hiddenBlackMoves.contains(point)) {
                    node.addStone(board, point, StoneColor.HIDDEN_WHITE)
                }
            }
            game.root.name = "Hidden Moves"
        }
        game.playerToMove.yourTurn()

        return null
    }

    override fun capturedStones(points: Set<Point>) {
        points.forEach { point ->

            hiddenBlackMoves.filter { it.isTouching(point) }.forEach {
                reveal(it)
            }
            hiddenWhiteMoves.filter { it.isTouching(point) }.forEach {
                reveal(it)
            }
        }
    }

    fun reveal(point: Point) {
        val realColor = board.getStoneAt(point).realColor()
        board.setStoneAt(point, realColor)
        game.addMark(CircleMark(point))

        val wasColor = if (hiddenWhiteMoves.remove(point)) StoneColor.WHITE else StoneColor.BLACK
        hiddenBlackMoves.remove(point)

        game.root.removeStone(game.board, point)
        game.root.addStone(game.board, point, wasColor)
    }

    override fun gameEnded(winner: Player?) {
        hiddenBlackMoves.toList().forEach { reveal(it) }
        hiddenWhiteMoves.toList().forEach { reveal(it) }
    }

}
