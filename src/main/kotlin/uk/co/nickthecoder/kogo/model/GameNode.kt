package uk.co.nickthecoder.kogo.model

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

    open fun bodyApply(game: Game) {
        removedStones.forEach { point, _ ->
            game.board.removeStoneAt(point)
        }
        addedStones.forEach { point, color ->
            game.setupStone(point, color)
        }
    }

    fun apply(game: Game) {

        if (this === game.root && game.currentNode === this) {
        } else {
            if (parent != game.currentNode) {
                throw IllegalArgumentException("$this is not a child of the current node : ${game.currentNode}")
            }
        }
        bodyApply(game)
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

    abstract fun copy(): GameNode

    /**
     * Copy the data not set by the constructor
     */
    open protected fun copyDetails(into: GameNode) {
        into.moveNumber = this.moveNumber
        this.addedStones.forEach { key, value -> into.addedStones.put(key, value) }
        this.removedStones.forEach { key, value -> into.removedStones.put(key, value) }
        this.mutableMarks.forEach { into.mutableMarks.add(it) }
        into.comment = this.comment
        into.name = this.name
        into.moveAnotation = this.moveAnotation
        into.nodeAnotation = this.nodeAnotation
    }
}

class SetupNode(colorToPlay: StoneColor) : GameNode(colorToPlay) {

    override fun toString() = "#$moveNumber SetupNode" // TODO count of stones added

    override fun copy(): SetupNode {
        val copy = SetupNode(colorToPlay)
        copyDetails(copy)
        return copy
    }
}

class PassNode(val color: StoneColor) : GameNode(color.opposite()) {

    override fun sameAs(node: GameNode) = node is PassNode

    override fun toString() = "#$moveNumber PassNode"

    override fun copy(): PassNode {
        val copy = PassNode(colorToPlay)
        copyDetails(copy)
        return copy
    }
}

class MoveNode(var point: Point, var color: StoneColor) : GameNode(color.opposite()) {

    var takenStones = setOf<Point>()

    override fun bodyApply(game: Game) {
        if (game.board.getStoneAt(point) != StoneColor.NONE) {
            game.board.removeStoneAt(point)
        }
        game.board.setStoneAt(point, color)
        takenStones = game.board.removeTakenStones(point)
        if (color == StoneColor.BLACK) {
            game.blackCaptures += takenStones.size
        } else {
            game.whiteCaptures += takenStones.size
        }
    }

    override fun bodyTakeBack(game: Game) {
        game.board.removeStoneAt(point)
        val removedColor = StoneColor.opposite(color)
        takenStones.forEach { point ->
            game.board.setStoneAt(point, removedColor)
        }
        if (color == StoneColor.BLACK) {
            game.blackCaptures -= takenStones.size
        } else {
            game.whiteCaptures -= takenStones.size
        }
    }

    override fun sameAs(node: GameNode) = node is MoveNode && this.point == node.point && this.color == node.color

    override fun toString() = "#$moveNumber MoveNode $color @ $point"

    override fun copy(): MoveNode {
        val copy = MoveNode(point, color)
        copyDetails(copy)
        return copy
    }
}
