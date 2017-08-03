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

import uk.co.nickthecoder.goko.*
import uk.co.nickthecoder.goko.preferences.Preferences
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Note, this class is NOT thread safe, but this shouldn't be a problem, as all interactions should be done in the
 * JavaFX thread. Also, all GameListeners will be notified from the JavaFX Thread.
 * There are a few places where threading is used : GnuGo and the countdown timer in GameInfoView.
 */
class Game(size: Int) {

    var file: File? = null

    val metaData = GameMetaData(this)

    val board = Board(size, this)

    var playerToMove: Player = EditGamePlayer(this, StoneColor.BLACK)

    var players = mutableMapOf<StoneColor, Player>()

    val listeners = mutableListOf<GameListener>()

    var root = SetupNode(playerToMove.color)

    val currentNode: GameNode
        get() = pCurrentNode

    private var pCurrentNode: GameNode = root

    var whiteCaptures: Int = 0

    var blackCaptures: Int = 0

    var variation: GameVariation = StandardGo(this)

    private var gnuGo: GnuGo? = null

    init {
        addPlayer(playerToMove)
        addPlayer(EditGamePlayer(this, StoneColor.WHITE))
    }

    fun start() {
        metaData.whiteName = players[StoneColor.WHITE]!!.label
        metaData.blackName = players[StoneColor.BLACK]!!.label
        variation.start()

        updatedMetaData()
    }

    fun createGnuGo(): GnuGo {
        gnuGo?.let { return it }
        players.values.forEach {
            if (it is GnuGoPlayer) {
                gnuGo = it.gnuGo
                return it.gnuGo
            }
        }
        val result = GnuGo(this, 10)
        result.start()
        gnuGo = result
        return result
    }

    fun addPlayer(player: Player) {
        players.put(player.color, player)
        if (playerToMove.color == player.color) {
            playerToMove = player
        }
        metaData.blackName = players[StoneColor.BLACK]?.label ?: ""
        metaData.whiteName = players[StoneColor.WHITE]?.label ?: ""
    }

    fun resign(player: Player) {
        val winner = otherPlayer(player)
        gameFinished(winner, winner.letter + "+Resign")
    }

    fun lostOnTime(player: Player) {
        val winner = otherPlayer(player)
        gameFinished(winner, winner.letter + "+Time")
    }

    fun gameFinished(winner: Player?, matchResult: String = "") {
        metaData.result = matchResult
        listeners.forEach {
            it.gameEnded(winner)
        }
    }

    fun countEndGame() {
        val scorer = FinalScore(this)
        scorer.score { scoreString ->
            val winnerLetter = if (scoreString.isEmpty()) "" else scoreString.substring(0, 1)
            if (winnerLetter == "W" || winnerLetter == "B") {
                val winColor = if (winnerLetter == "B") StoneColor.BLACK else if (winnerLetter == "W") StoneColor.WHITE else null
                val winner = players[winColor]
                gameFinished(winner, scoreString)
            } else {
                playerToMove.yourTurn()
            }
        }
    }

    fun otherPlayer(player: Player): Player {
        val nextColor = StoneColor.opposite(player.color)
        return players[nextColor]!!
    }

    fun pass(color: StoneColor, onMainLine: Boolean = true) {
        if (color != playerToMove.color) {
            throw IllegalArgumentException("It is ${playerToMove.color}'s turn")
        }
        val node = PassNode(playerToMove.color)
        apply(addNode(node, onMainLine))
        if (currentNode.parent is PassNode) {
            countEndGame()
        }
    }

    fun move(point: Point, color: StoneColor, onMainLine: Boolean = true) {
        if (color != playerToMove.color) {
            throw IllegalArgumentException("It is ${playerToMove.color}'s turn")
        }

        if (!color.isStone()) {
            throw IllegalArgumentException("Must play black or white")
        }
        if (board.getStoneAt(point) != StoneColor.NONE) {
            throw IllegalArgumentException("This point is already taken")
        }
        val node = MoveNode(point, color)
        apply(addNode(node, onMainLine))
    }

    var autoPlay: Boolean = true

    internal fun nodeChanged(node: GameNode) {
        listeners.forEach {
            it.nodeChanged(node)
        }
    }

    fun canPlayAt(point: Point): Boolean {
        if (!board.contains(point) || board.getStoneAt(point) != StoneColor.NONE) {
            return false
        }
        val copy = board.copy()
        copy.setStoneAt(point, playerToMove.color)
        val takenStones = copy.removeTakenStones(point)
        if (takenStones.size == 0) {
            if (copy.checkLiberties(point) != null) {
                return false
            }
        }
        if (takenStones.size == 1) {
            // Check for ko, by comparing the hashes of previous board positions.
            val newHash = copy.hashCode()
            var node: GameNode? = currentNode
            while (node != null) {
                if (node.boardHash == newHash) {
                    return false
                }
                node = node.parent
            }
        }
        return true
    }

    fun getMarkAt(point: Point): Mark? {
        return currentNode.getMarkAt(point)
    }

    fun addMark(mark: Mark) {
        removeMark(mark.point)
        currentNode.addMark(mark)
        for (listener in listeners) {
            listener.addedMark(mark)
        }
    }

    fun removeMark(point: Point) {
        val mark = currentNode.removeMark(point)
        if (mark != null) {
            for (listener in listeners) {
                listener.removedMark(mark)
            }
        }
    }

    fun deleteBranch() {
        if (currentNode == root) {
            root.children.forEach { it.parent = null }
            root.children.clear()
            listeners.forEach {
                it.nodeChanged(root)
            }
        } else {
            val node = currentNode
            currentNode.parent?.children?.remove(node)
            moveBack()
            node.parent = null
        }
    }

    fun clearMarks() {
        currentNode.marks.toList().forEach { removeMark(it.point) }
    }

    fun updatedMetaData() {
        for (listener in listeners) {
            listener.updatedMetaData()
        }
    }

    fun tidyUp() {
        players.values.forEach { player ->
            player.tidyUp()
        }
        gnuGo?.tidyUp()
    }

    fun addNode(node: GameNode, onMainLine: Boolean): GameNode {

        currentNode.children.forEach { child ->
            if (child.sameAs(node)) {
                return child
            }
        }

        node.moveNumber = currentNode.moveNumber + 1
        node.parent = currentNode
        if (onMainLine) {
            currentNode.children.add(0, node)
        } else {
            currentNode.children.add(node)
        }
        return node
    }

    fun apply(node: GameNode) {
        autoPlay = true
        if (!currentNode.children.contains(node) && node !== root) {
            throw IllegalStateException("Node is not a child of the current node")
        }

        if (node is SetupNode) {
            node.removedStones.forEach { point, _ ->
                board.removeStoneAt(point)
            }
            node.addedStones.forEach { point, color ->
                board.setStoneAt(point, color)
            }
        }

        if (node is MoveNode) {
            if (board.getStoneAt(node.point) != StoneColor.NONE) {
                println("Hmm, there's already a stone at ${node.point}")
                //throw IllegalStateException("Already a stone at ${node.point}")
            }
            board.setStoneAt(node.point, node.color)
            node.takenStones = board.removeTakenStones(node.point)
            if (node.color == StoneColor.BLACK) {
                blackCaptures += node.takenStones.size
            } else {
                whiteCaptures += node.takenStones.size
            }
        }

        pCurrentNode = node

        if (node !is SetupNode) {
            playerToMove = otherPlayer(playerToMove)
        }

        node.boardHash = board.hashCode()

        listeners.forEach {
            it.madeMove(node)
        }

        if (Preferences.advancedPreferences.checkGnuGoSyncP.value == true) {
            createGnuGo().checkBoard()
        }

        if (node is MoveNode && node.takenStones.isNotEmpty()) {
            variation.capturedStones(node.colorToPlay, node.takenStones)
        }

        if (autoPlay) {
            playerToMove.yourTurn()
            autoPlay = false
        }
    }

    fun unApply(node: GameNode) {
        if (node != currentNode) {
            throw IllegalStateException("Can only unApply the current node")
        }
        val parent = node.parent ?: throw IllegalStateException("Cannot un-apply the root node")

        if (node is SetupNode) {
            node.removedStones.forEach { point, color ->
                board.setStoneAt(point, color)
            }
            node.addedStones.forEach { point, _ ->
                board.removeStoneAt(point)
            }
        }

        if (node is MoveNode) {
            board.removeStoneAt(node.point)
            val opposite = node.color.opposite()
            node.takenStones.forEach { point ->
                board.setStoneAt(point, opposite)
            }
            if (node.color == StoneColor.BLACK) {
                blackCaptures -= node.takenStones.size
            } else {
                whiteCaptures -= node.takenStones.size
            }
        }

        if (node !is SetupNode) {
            playerToMove = otherPlayer(playerToMove)
        }

        pCurrentNode = parent
        listeners.forEach {
            it.undoneMove(node)
        }
    }

    fun moveBack(n: Int = 1) {
        for (foo in 1..n) {
            val parent = currentNode.parent
            parent ?: return
            unApply(currentNode)
        }
    }

    fun moveForward(n: Int = 1) {
        for (foo in 1..n) {
            if (currentNode.children.isEmpty()) {
                return
            }
            val nextNode = currentNode.children[0]
            apply(nextNode)
        }
    }

    fun rewindTo(gameNode: GameNode) {
        while (currentNode !== gameNode && currentNode !== root) {
            moveBack()
        }
    }

    fun dumpTree() {

        fun dump(indent: Int, node: GameNode) {
            print(" ".repeat(indent * 4))
            println(node)
            for (child in node.children) {
                dump(indent + 1, child)
            }
        }
        dump(0, root)
    }

    fun copy(): Game {
        val baos = ByteArrayOutputStream()
        val writer = SGFWriter(baos)

        writer.write(this)

        println(baos)

        val reader = SGFReader(ByteArrayInputStream(baos.toByteArray()))
        val result = reader.read()
        result.file = this.file

        result.apply(result.root)
        return result
    }
}
