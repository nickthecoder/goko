package uk.co.nickthecoder.goko.model

class HiddenMoveGo(game: Game, val hiddenMoveCountBlack: Int, val hiddenMoveCountWhite: Int) : StandardGo(game) {

    var isSettingUp: Boolean = true

    var hiddenBlackMoves = listOf<Point>()
    var hiddenWhiteMoves = listOf<Point>()

    override fun canPlayAt(point: Point?): Boolean {
        if (point != null) {
            val color = board.getStoneAt(point)
            if (color == StoneColor.HIDDEN_BLACK) {
                board.setStoneAt(point, StoneColor.BLACK)
            } else if (color == StoneColor.HIDDEN_WHITE) {
                board.setStoneAt(point, StoneColor.WHITE)
            }
        }
        return super.canPlayAt(point)
    }

    /**
     * Make a move at point, or null for a pass.
     */
    override fun playAt(point: Point?, onMainLine: Boolean): String? {
        if (isSettingUp) {

            if (point == null) {
                return endSetup(onMainLine)
            } else {
                if (game.getMarkAt(point) == null) {
                    game.addMark(TerritoryMark(point, game.playerToMove.color))
                } else {
                    game.removeMark(point)
                }
            }
            return null

        } else {
            return super.playAt(point, onMainLine)
        }
    }

    private fun requiredHiddenCount(): Int {
        if (game.playerToMove.color == StoneColor.BLACK) {
            return hiddenMoveCountBlack
        } else {
            return hiddenMoveCountWhite
        }
    }

    private fun endSetup(onMainLine: Boolean): String? {
        val marks = game.currentNode.marks
        if (marks.size != requiredHiddenCount()) {
            return "You must play ${requiredHiddenCount()} before passing"
        }

        if (game.playerToMove.color == StoneColor.BLACK) {
            hiddenBlackMoves = marks.map { it.point }
            game.pass(onMainLine)

        } else {
            hiddenWhiteMoves = marks.map { it.point }
            isSettingUp = false
            val node = SetupNode(StoneColor.BLACK)
            hiddenBlackMoves.forEach { point ->
                if (hiddenWhiteMoves.contains(point)) {
                    node.addStone(board, point, StoneColor.HIDDEN_BOTH)
                } else {
                    node.addStone(board, point, StoneColor.BLACK)
                }
            }
            hiddenWhiteMoves.forEach { point ->
                if (!hiddenBlackMoves.contains(point)) {
                    node.addStone(board, point, StoneColor.HIDDEN_WHITE)
                }
            }
            game.addNode(node, onMainLine)
            game.apply(node)

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
        val color = board.getStoneAt(point)
        if (color == StoneColor.HIDDEN_WHITE) {
            board.setStoneAt(point, StoneColor.WHITE)
            game.addMark(CircleMark(point))
        } else if (color == StoneColor.HIDDEN_WHITE) {
            board.setStoneAt(point, StoneColor.BLACK)
            game.addMark(CircleMark(point))
        }
    }

}
