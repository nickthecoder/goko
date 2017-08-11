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
package uk.co.nickthecoder.goko.model

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

    fun getMarkAt(point: Point): Mark? {
        mutableMarks.forEach { mark ->
            if (point == mark.point) {
                return mark
            }
        }
        return null
    }

    fun isMainLine(): Boolean {
        var node = this
        while (node.parent != null) {
            if (node.parent!!.children[0] !== node) {
                return false
            }
            node = node.parent!!
        }
        return true
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
        if (board.game.currentNode == this) {
            board.setStoneAt(point, color)
        }
    }

    fun removeStone(board: Board, point: Point) {
        val oldColor = board.getStoneAt(point)
        if (oldColor.isStone()) {
            if (removedStones[point] == null) {
                removedStones.put(point, oldColor)
            }
        }
        if (board.game.currentNode == this) {
            board.removeStoneAt(point)
        }
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
