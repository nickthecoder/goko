package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.Player

abstract class GameNode() {

    var moveNumber: Int = 0

    val children = mutableListOf<GameNode>()

    var parent: GameNode? = null

    var addedStones = mutableMapOf<Point, StoneColor>()

    var removedStones = mutableMapOf<Point, StoneColor>()

    val marks = mutableListOf<Mark>()

    var comment: String = ""

    var name: String = ""

    var statuses = mutableSetOf<NodeStatus>()

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
        println("Applying node $this")
        removedStones.forEach { point, _ ->
            game.board.removeStoneAt(point, byPlayer)
        }
        addedStones.forEach { point, color ->
            println("Applying stone $color @ $point")
            game.board.setStoneAt(point, color, byPlayer)
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
        game.moved()
        game.currentNode = this
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

class PassNode() : GameNode() {

    override fun sameAs(node: GameNode) = node is PassNode

    override fun toString() = "#$moveNumber PassNode"
}

class SetupNode() : GameNode() {

    override fun toString() = "#$moveNumber SetupNode" // TODO count of stones added
}

class MoveNode(var point: Point, var color: StoneColor) : GameNode() {

    var takenStones = setOf<Point>()

    override fun bodyApply(game: Game, byPlayer: Player?) {
        if (game.board.getStoneAt(point) != StoneColor.NONE) {
            game.board.removeStoneAt(point, byPlayer)
        }
        game.board.setStoneAt(point, color, byPlayer = byPlayer)
        takenStones = game.board.removeTakenStones(point, byPlayer)
    }

    override fun bodyTakeBack(game: Game) {
        game.board.removeStoneAt(point, null)
        val removedColor = StoneColor.opposite(color)
        takenStones.forEach { point ->
            game.board.setStoneAt(point, removedColor, null)
        }
    }

    override fun sameAs(node: GameNode) = node is MoveNode && this.point == node.point && this.color == node.color

    override fun toString() = "#$moveNumber MoveNode $color @ $point"
}
