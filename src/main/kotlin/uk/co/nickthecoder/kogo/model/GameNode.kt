package uk.co.nickthecoder.kogo.model

abstract class GameNode(var colorToPlay: StoneColor) {

    var moveNumber: Int = 0

    val children = mutableListOf<GameNode>()

    var parent: GameNode? = null

    private val mutableMarks = mutableListOf<Mark>()

    val marks: List<Mark> = mutableMarks

    var comment: String = ""

    var name: String = ""

    var moveAnnotation: MoveAnnotation? = null

    var nodeAnnotation: NodeAnnotation? = null

    var nodeAnnotationVery: Boolean = false

    var boardHash: Int = 0

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
        marks.forEach { mark ->
            if (mark is LabelMark && mark.text == text) {
                return true
            }
        }
        return false
    }

    fun hasMarkAt(point: Point): Boolean {
        mutableMarks.forEach { mark ->
            if (point == mark.point) {
                return true
            }
        }
        return false
    }

    open fun sameAs(node: GameNode) = false

    abstract fun copy(): GameNode

    /**
     * Copy the data not set by the constructor
     */
    open protected fun copyDetails(into: GameNode) {
        into.moveNumber = this.moveNumber
        this.mutableMarks.forEach { into.mutableMarks.add(it) }
        into.comment = this.comment
        into.name = this.name
        into.moveAnnotation = this.moveAnnotation
        into.nodeAnnotation = this.nodeAnnotation
    }
}

class SetupNode(colorToPlay: StoneColor) : GameNode(colorToPlay) {


    var addedStones = mutableMapOf<Point, StoneColor>()

    var removedStones = mutableMapOf<Point, StoneColor>()

    override fun toString() = "#$moveNumber SetupNode"

    fun addStone(board: Board, point: Point, color: StoneColor) {
        val oldColor = board.getStoneAt(point)
        if (oldColor.isStone()) {
            if (removedStones[point] == null) {
                removedStones.put(point, oldColor)
            }
        }
        addedStones.put(point, color)
        board.setStoneAt(point, color)
    }

    fun removeStone(board: Board, point: Point) {
        val oldColor = board.getStoneAt(point)
        if (oldColor.isStone()) {
            if (removedStones[point] == null) {
                removedStones.put(point, oldColor)
            }
        }
        board.removeStoneAt(point)
    }

    override fun copy(): SetupNode {
        val copy = SetupNode(colorToPlay)
        copyDetails(copy)
        this.addedStones.forEach { key, value -> copy.addedStones.put(key, value) }
        this.removedStones.forEach { key, value -> copy.removedStones.put(key, value) }

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

    override fun sameAs(node: GameNode) = node is MoveNode && this.point == node.point && this.color == node.color

    override fun toString() = "#$moveNumber MoveNode $color @ $point"

    override fun copy(): MoveNode {
        val copy = MoveNode(point, color)
        copyDetails(copy)
        return copy
    }
}
