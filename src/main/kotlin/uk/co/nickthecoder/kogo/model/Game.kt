package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.kogo.GnuGoPlayer
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.Player
import java.io.File

class Game(sizeX: Int, sizeY: Int) {

    var file: File? = null

    val metaData = GameMetaData()

    val board = Board(sizeX, sizeY, this)

    var playerToMove: Player = LocalPlayer(StoneColor.BLACK)

    var handicap = 0;

    var players = mutableMapOf<StoneColor, Player>()

    val gameListeners = mutableListOf<GameListener>()

    val handicaps = listOf(
            Point(0, 0), Point(2, 2), Point(2, 0), Point(0, 2),
            Point(1, 1),
            Point(1, 0), Point(1, 2), Point(0, 1), Point(2, 1))

    var root = SetupNode(playerToMove.color)

    var currentNode: GameNode = root

    init {
        metaData.boardSize = Math.max(sizeX, sizeY)

        addPlayer(playerToMove)
        addPlayer(LocalPlayer(StoneColor.WHITE))
    }

    fun placeHandicap(n: Int) {
        var start = 3
        var jump = 6
        if (board.sizeX < 19) {
            if (board.sizeX < 13) {
                start = 1
                jump = 4
            } else {
                start = 2
                jump = 5
            }
        }

        for (i in 0..n - 1) {
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
        playerToMove = players.get(if (handicap == 0) StoneColor.BLACK else StoneColor.WHITE)!!
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
        gameListeners.forEach {
            it.matchResult(this, winner)
        }
    }

    var awaitingFinalCount = false

    fun countEndGame() {
        val counter = GnuGoPlayer(this, StoneColor.NONE)
        counter.start()
        // Tell the counter where the stones are
        for (x in 0..board.sizeX - 1) {
            for (y in 0..board.sizeY - 1) {
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

    private fun addAndApplyNode(node: GameNode, byPlayer: Player?) {
        autoPlay = true
        currentNode.children.forEach { child ->
            if (child.sameAs(node)) {
                child.apply(this, null)
                return
            }
        }

        currentNode.children.add(node)
        node.parent = currentNode
        node.apply(this, byPlayer)
    }

    fun pass() {
        val node = PassNode(playerToMove.color.opposite())
        addAndApplyNode(node, null)
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
        val dummyPlayer = LocalPlayer(StoneColor.NONE)
        copy.setStoneAt(point, playerToMove.color, dummyPlayer)
        copy.removeTakenStones(point, dummyPlayer)
        if (copy.checkLiberties(point) != null) {
            return false
        }
        // TODO Check for kos
        return true
    }

    fun addMark(mark: Mark) {
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

    fun undo() {
        // TODO move If it is a non-local player to move, ensure their move is ignored.
        // (This may have already happened, or may happen soon!)
        moveBack()
    }

    fun moveBack() {
        val parent = currentNode.parent
        if (parent != null) {
            currentNode.takeBack(this)
            currentNode = parent
        }
        moved()
    }

    fun moveForward() {
        if (currentNode.children.isNotEmpty()) {
            val nextNode = currentNode.children[0]
            nextNode.apply(this)
            currentNode = nextNode
        }
        moved()
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
        println("Rewinding. To play = ${playerToMove.color}")
        while (currentNode !== gameNode && currentNode !== root) {
            moveBack()
            println("Moved back. To play = ${playerToMove.color}")
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
}
