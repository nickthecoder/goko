package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.*
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

    val handicaps = listOf(
            Point(0, 0), Point(2, 2), Point(2, 0), Point(0, 2),
            Point(1, 1),
            Point(1, 0), Point(1, 2), Point(0, 1), Point(2, 1))

    var root = SetupNode(playerToMove.color)

    var currentNode: GameNode = root

    var whiteCaptures: Int = 0

    var blackCaptures: Int = 0

    /**
     * The number of handicap stones the black player still has to play.
     * Is zero when using fixed handicap points (on the star points).
     */
    var freeHandicaps: Int = 0

    private var gnuGo: GnuGo? = null

    init {
        addPlayer(playerToMove)
        addPlayer(EditGamePlayer(this, StoneColor.WHITE))
    }

    fun start() {
        metaData.whiteName = players[StoneColor.WHITE]!!.label
        metaData.blackName = players[StoneColor.BLACK]!!.label
        if (metaData.fixedHandicaptPoints) {
            placeHandicap()
            playerToMove = players[if (metaData.handicap!! < 2) StoneColor.BLACK else StoneColor.WHITE]!!
            playerToMove.yourTurn()
            root.colorToPlay = playerToMove.color
        } else {
            playerToMove = players[StoneColor.BLACK]!!
            if (metaData.handicap!! > 2) {
                freeHandicaps = metaData.handicap!!
                playerToMove.placeHandicap()
            } else {
                playerToMove.yourTurn()
            }
        }
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
        for (y in 0..board.size - 1) {
            for (x in 0..board.size - 1) {
                val color = board.getStoneAt(x, y)
                if (color.isStone()) {
                    result.addStone(color, Point(x, y))
                }
            }
        }
        gnuGo = result
        return result
    }

    fun placeHandicap() {
        val start: Int
        if (board.size < 13) {
            start = 2
        } else {
            start = 3
        }
        val jump = (board.size - 1) / 2 - start

        for (i in 0..metaData.handicap!! - 1) {
            val h = handicaps[i]
            val point = Point(start + h.x * jump, start + h.y * jump)
            root.addStone(board, point, StoneColor.BLACK)
            board.setStoneAt(point, StoneColor.BLACK)
        }
    }

    fun addPlayer(player: Player) {
        players.put(player.color, player)
        if (playerToMove.color == player.color) {
            playerToMove = player
        }
        metaData.blackName = players[StoneColor.BLACK]?.label ?: ""
        metaData.whiteName = players[StoneColor.WHITE]?.label ?: ""
    }

    fun setupStone(point: Point, color: StoneColor) {
        board.setStoneAt(point, color)
    }

    fun resign(player: Player) {
        val winner = otherPlayer(player)
        gameFinished(winner, winner.letter + "+Resign")
        countEndGame()
    }

    fun lostOnTime(player: Player) {
        val winner = otherPlayer(player)
        gameFinished(winner, winner.letter + "+Time")
        countEndGame()
    }

    fun gameFinished(winner: Player?, matchResult: String = "") {
        metaData.result = matchResult
        listeners.forEach {
            it.gameEnded(winner)
        }
    }

    var awaitingFinalCount = false

    fun countEndGame() {
        val scorer = ScoreEstimator(this)
        scorer.estimate { countedEndGame(it) }
    }

    fun countedEndGame(result: String) {
        val winChar = if (result.isEmpty()) "" else result.substring(0, 1)
        val winColor = if (winChar == "B") StoneColor.BLACK else if (winChar == "W") StoneColor.WHITE else null
        val winner = players.get(winColor)
        if (awaitingFinalCount) {
            gameFinished(winner, result)
        }
    }

    fun otherPlayer(player: Player): Player {
        val nextColor = StoneColor.opposite(player.color)
        return players.get(nextColor)!!
    }

    fun pass(onMainLine: Boolean = true) {
        val node = PassNode(playerToMove.color)
        addAndApplyNode(node, onMainLine)
        if (currentNode.parent is PassNode) {
            awaitingFinalCount = true
            countEndGame()
        }
    }

    fun move(point: Point, color: StoneColor, onMainLine: Boolean = true) {
        if (freeHandicaps > 0) {
            if (currentNode != root) {
                throw IllegalStateException("Can only place handicap stones in the root node")
            }
            freeHandicaps--
            root.addStone(board, point, color)
            nodeChanged(currentNode)

            if (freeHandicaps == 0) {
                root.colorToPlay = StoneColor.WHITE
                moved()
            }
            return
        }

        if (!color.isStone()) {
            throw IllegalArgumentException("Must play black or white")
        }
        if (board.getStoneAt(point) != StoneColor.NONE) {
            throw IllegalArgumentException("This point is already taken")
        }
        val node = MoveNode(point, color)
        addAndApplyNode(node, onMainLine)
    }

    var autoPlay: Boolean = true

    internal fun nodeChanged(node: GameNode) {
        listeners.forEach {
            it.nodeChanged(node)
        }
    }

    internal fun moved() {
        val node = currentNode
        playerToMove = players[node.colorToPlay]!!
        listeners.forEach {
            it.moved()
        }
        if (autoPlay) {
            playerToMove.yourTurn()
            autoPlay = false
        }
    }

    fun canPlayAt(point: Point): Boolean {
        if (!board.contains(point) || board.getStoneAt(point) != StoneColor.NONE) {
            return false
        }
        val copy = board.copy()
        copy.setStoneAt(point, playerToMove.color)
        copy.removeTakenStones(point)
        if (copy.checkLiberties(point) != null) {
            return false
        }
        // TODO Check for kos
        return true
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
            moved()
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

    fun addNode(node: GameNode, onMainLine: Boolean) {
        node.moveNumber = currentNode.moveNumber + 1
        node.parent = currentNode
        if (onMainLine) {
            currentNode.children.add(0, node)
        } else {
            currentNode.children.add(node)
        }
    }

    private fun addAndApplyNode(node: GameNode, onMainLine: Boolean) {
        autoPlay = true
        currentNode.children.forEach { child ->
            if (child.sameAs(node)) {
                child.apply(this)
                return
            }
        }
        addNode(node, onMainLine)
        node.apply(this)
    }

    fun moveBack(n: Int = 1) {
        for (foo in 1..n) {
            val parent = currentNode.parent
            parent ?: return

            currentNode.takeBack(this)
            currentNode = parent
            moved()
        }
    }

    fun moveForward(n: Int = 1) {
        for (foo in 1..n) {
            if (currentNode.children.isEmpty()) {
                return
            }
            val nextNode = currentNode.children[0]
            nextNode.apply(this)
            currentNode = nextNode
            moved()
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

        result.root.apply(result)
        return result
    }
}
