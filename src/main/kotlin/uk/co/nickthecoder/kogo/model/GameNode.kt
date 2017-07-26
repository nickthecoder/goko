package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.Player

abstract class GameNode(var colorToPlay: StoneColor) {

    var moveNumber: Int = 0

    val children = mutableListOf<GameNode>()

    var parent: GameNode? = null

    var addedStones = mutableMapOf<Point, StoneColor>()

    var removedStones = mutableMapOf<Point, StoneColor>()

    private val mutableMarks = mutableListOf<Mark>()

    val marks: List<Mark> = mutableMarks

    var comment: String = ""

    var name: String = ""

    var moveAnotation: MoveAnotation? = null

    var nodeAnotation: NodeAnotation? = null

    var nodeAnotationVery: Boolean = false

    fun addMark(mark: Mark) {
        removeMark(mark.point)
        mutableMarks.add(mark)
    }

    fun removeMark(point: Point): Mark? {
        for (mark in mutableMarks) {
            if (point == mark.point) {
                mutableMarks.remove(mark)
                return mark
            }
        }
        return null
    }

    fun hasLabelMark(text: String): Boolean {
        for (mark in marks) {
            if (mark is LabelMark && mark.text == text) {
                return true
            }
        }
        return false
    }

    fun hasMarkAt(point: Point): Boolean {
        for (mark in mutableMarks) {
            if (point == mark.point) {
                return true
            }
        }
        return false
    }

    fun addStoneOnly(board: Board, point: Point, color: StoneColor) {
        val oldColor = board.getStoneAt(point)
        if (oldColor.isStone()) {
            if (removedStones.get(point) == null) {
                removedStones.put(point, oldColor)
            }
        }
        addedStones.put(point, color)
    }

    fun addStone(board: Board, point: Point, color: StoneColor) {
        addStoneOnly(board, point, color)
        board.setStoneAt(point, color)
    }

    fun removeStoneOnly(board: Board, point: Point) {
        val oldColor = board.getStoneAt(point)
        if (oldColor.isStone()) {
            if (removedStones.get(point) == null) {
                removedStones.put(point, oldColor)
            }
        }
    }

    fun removeStone(board: Board, point: Point) {
        removeStoneOnly(board, point)
        board.removeStoneAt(point)
    }

    open fun bodyApply(game: Game, byPlayer: Player?) {
        removedStones.forEach { point, _ ->
            game.board.removeStoneAt(point, byPlayer)
        }
        addedStones.forEach { point, color ->
            game.setupStone(point, color)
        }
    }

    fun apply(game: Game, byPlayer: Player? = null) {

        if (this === game.root && game.currentNode === this) {
        } else {
            if (parent != game.currentNode) {
                throw IllegalArgumentException("$this is not a child of the current node : ${game.currentNode}")
            }
        }
        bodyApply(game, byPlayer)
        game.currentNode = this
        game.moved()
    }

    fun takeBack(game: Game) {
        if (this != game.currentNode) {
            throw IllegalArgumentException("$this is not the current node : ${game.currentNode}")
        }
        bodyTakeBack(game)
        game.moved()
    }

    open fun bodyTakeBack(game: Game) {
        addedStones.forEach { point, _ ->
            game.board.removeStoneAt(point)
        }
        removedStones.forEach { point, color ->
            game.board.setStoneAt(point, color)
        }
    }

    open fun sameAs(node: GameNode) = false
}


class SetupNode(colorToPlay: StoneColor) : GameNode(colorToPlay) {

    override fun toString() = "#$moveNumber SetupNode" // TODO count of stones added
}

class PassNode(val color: StoneColor) : GameNode(color.opposite()) {

    override fun sameAs(node: GameNode) = node is PassNode

    override fun toString() = "#$moveNumber PassNode"
}

class MoveNode(var point: Point, var color: StoneColor) : GameNode(color.opposite()) {

    var takenStones = setOf<Point>()

    override fun bodyApply(game: Game, byPlayer: Player?) {
        if (game.board.getStoneAt(point) != StoneColor.NONE) {
            game.board.removeStoneAt(point, byPlayer)
        }
        game.board.setStoneAt(point, color, byPlayer = byPlayer)
        takenStones = game.board.removeTakenStones(point, byPlayer)
        if (color == StoneColor.BLACK) {
            game.blackCaptures += takenStones.size
        } else {
            game.whiteCaptures += takenStones.size
        }
    }

    override fun bodyTakeBack(game: Game) {
        game.board.removeStoneAt(point, null)
        val removedColor = StoneColor.opposite(color)
        takenStones.forEach { point ->
            game.board.setStoneAt(point, removedColor, null)
        }
        if (color == StoneColor.BLACK) {
            game.blackCaptures -= takenStones.size
        } else {
            game.whiteCaptures -= takenStones.size
        }
    }

    override fun sameAs(node: GameNode) = node is MoveNode && this.point == node.point && this.color == node.color

    override fun toString() = "#$moveNumber MoveNode $color @ $point"
}
