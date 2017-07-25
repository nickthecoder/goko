package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.GnuGoPlayer
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.Player
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

class Game(size: Int) {

    var file: File? = null

    val metaData = GameMetaData(this)

    val board = Board(size, this)

    var playerToMove: Player = LocalPlayer(this, StoneColor.BLACK)

    var handicap = 0

    var players = mutableMapOf<StoneColor, Player>()

    val gameListeners = mutableListOf<GameListener>()

    val handicaps = listOf(
            Point(0, 0), Point(2, 2), Point(2, 0), Point(0, 2),
            Point(1, 1),
            Point(1, 0), Point(1, 2), Point(0, 1), Point(2, 1))

    var root = SetupNode(playerToMove.color)

    var currentNode: GameNode = root

    init {
        addPlayer(playerToMove)
        addPlayer(LocalPlayer(this, StoneColor.WHITE))
    }

    fun placeHandicap() {
        var start = 3
        var jump = 6
        if (board.size < 19) {
            if (board.size < 13) {
                start = 1
                jump = 4
            } else {
                start = 2
                jump = 5
            }
        }

        for (i in 0..metaData.handicap - 1) {
            val h = handicaps[i]
            val point = Point(start + h.x * jump, start + h.y * jump)
            setupStone(point, StoneColor.BLACK)
        }
    }

    fun addPlayer(player: Player) {
        players.put(player.color, player)
        if (playerToMove.color == player.color) {
            playerToMove = player
        }
    }

    fun start() {
        playerToMove = players[if (handicap == 0) StoneColor.BLACK else StoneColor.WHITE]!!
        metaData.whitePlayer = players[StoneColor.WHITE]!!.label
        metaData.blackPlayer = players[StoneColor.BLACK]!!.label
        placeHandicap()
        playerToMove.yourTurn()
    }

    fun setupStone(point: Point, color: StoneColor) {
        board.setStoneAt(point, color, null)
    }

    fun resign(player: Player) {
        gameFinished(otherPlayer(player), player.letter + "+R")
        countEndGame()
    }

    fun gameFinished(winner: Player?, matchResult: String = "") {
        metaData.matchResult = matchResult
        gameListeners.forEach {
            it.matchResult(this, winner)
        }
    }

    var awaitingFinalCount = false

    fun countEndGame() {
        val counter = GnuGoPlayer(this, StoneColor.NONE)
        counter.start()
        // Tell the counter where the stones are
        for (x in 0..board.size - 1) {
            for (y in 0..board.size - 1) {
                val color = board.getStoneAt(x, y)
                if (color != StoneColor.NONE) {
                    counter.stoneChanged(Point(x, y), null)
                }
            }
        }
        counter.countGame()
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

    fun pass(byPlayer: Player) {
        val node = PassNode(playerToMove.color)
        addAndApplyNode(node, byPlayer)
        if (currentNode is PassNode) {
            awaitingFinalCount = true
            countEndGame()
        }
    }

    fun move(point: Point, color: StoneColor, byPlayer: Player) {
        if (color != StoneColor.BLACK && color != StoneColor.WHITE) {
            throw IllegalArgumentException("Must play black or white")
        }
        if (board.getStoneAt(point) != StoneColor.NONE) {
            throw IllegalArgumentException("This point is already taken")
        }
        val node = MoveNode(point, color)
        addAndApplyNode(node, byPlayer)
    }

    var autoPlay: Boolean = true

    internal fun moved() {
        val node = currentNode
        playerToMove = players.get(node.colorToPlay)!!
        gameListeners.forEach {
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
        val dummyPlayer = LocalPlayer(this, StoneColor.NONE)
        copy.setStoneAt(point, playerToMove.color, dummyPlayer)
        copy.removeTakenStones(point, dummyPlayer)
        if (copy.checkLiberties(point) != null) {
            return false
        }
        // TODO Check for kos
        return true
    }

    fun addMark(mark: Mark) {
        removeMark(mark.point)
        currentNode.addMark(mark)
        for (listener in gameListeners) {
            listener.addedMark(mark)
        }
    }

    fun removeMark(point: Point) {
        val mark = currentNode.removeMark(point)
        if (mark != null) {
            for (listener in gameListeners) {
                listener.removedMark(mark)
            }
        }
    }

    fun updatedCurrentNode() {
        for (listener in gameListeners) {
            listener.updatedCurrentNode()
        }
    }

    fun tidyUp() {
        players.values.forEach { player ->
            player.tidyUp()
        }
    }

    fun addNode(node: GameNode) {
        node.moveNumber = currentNode.moveNumber + 1
        node.parent = currentNode
        currentNode.children.add(node)
    }

    private fun addAndApplyNode(node: GameNode, byPlayer: Player?) {
        autoPlay = true
        currentNode.children.forEach { child ->
            if (child.sameAs(node)) {
                child.apply(this, byPlayer)
                return
            }
        }
        addNode(node)
        node.apply(this, byPlayer)
    }

    fun moveBack(n: Int = 1) {
        for (foo in 1..n) {
            val parent = currentNode.parent
            if (parent == null) {
                return
            }
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

    fun moveToStart() {
        while (currentNode.parent != null) {
            moveBack()
        }
    }

    fun moveToEnd() {
        while (currentNode.children.isNotEmpty()) {
            moveForward()
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

        return result
    }
}
