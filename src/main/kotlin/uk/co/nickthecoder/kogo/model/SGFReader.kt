package uk.co.nickthecoder.kogo.model

import java.io.File
import java.io.IOException
import java.io.Reader

/**
 * Reads a .sgf file.
 * See http://www.red-bean.com/sgf/sgf4.html
 */
class SGFReader(var file: File) {

    lateinit var reader: Reader

    val buffer = charArrayOf(' ')

    var unread: Char? = null

    var size = 19

    fun read(): Game {

        reader = file.bufferedReader()

        val sgfRoot = readTree()
        reader.close()

        dumpTree(sgfRoot)

        val game = Game(size, size)
        updateRootNode(game, sgfRoot)
        addChildren(game, sgfRoot)

        game.dumpTree()
        game.file = file
        return game
    }

    fun updateRootNode(game: Game, sgfNode: SGFNode) {
        val toPlay = toStoneColor(sgfNode.getPropertyValue("PL"))
        if (toPlay != null) {
            game.playerToMove = game.players.get(toPlay)!!
        }

        // TODO Add meta data such as play names, ranks etc

        updateNode(game, sgfNode)
    }

    fun updateNode(game: Game, sgfNode: SGFNode) {
        val currentNode = game.currentNode

        val whites = sgfNode.getPropertyValues("AW")
        if (whites != null) {
            whites.forEach { str ->
                val point = toPoint(game.board, str)
                currentNode.addStoneOnly(game.board, point, StoneColor.WHITE)
            }
        }
        val blacks = sgfNode.getPropertyValues("AB")
        if (blacks != null) {
            blacks.forEach { str ->
                val point = toPoint(game.board, str)
                currentNode.addStoneOnly(game.board, point, StoneColor.BLACK)
            }
        }

        val removed = sgfNode.getPropertyValues("AE")
        if (removed != null) {
            removed.forEach { str ->
                val point = toPoint(game.board, str)
                currentNode.removeStoneOnly(game.board, point)
            }
        }

        sgfNode.getPropertyValue("C")?.let {
            currentNode.comment = it
            println("Added comment $it")
        }
        sgfNode.getPropertyValue("N")?.let {
            currentNode.name = it
        }

        if (sgfNode.hasProperty("GW")) {
            currentNode.statuses.add(NodeStatus.GOOD_FOR_WHITE)
        }
        if (sgfNode.hasProperty("GB")) {
            currentNode.statuses.add(NodeStatus.GOOD_FOR_BLACK)
        }
        if (sgfNode.hasProperty("DM")) {
            currentNode.statuses.add(NodeStatus.EVEN)
        }
        if (sgfNode.hasProperty("HO")) {
            currentNode.statuses.add(NodeStatus.HOT_SPOT)
        }
        if (sgfNode.hasProperty("UC")) {
            currentNode.statuses.add(NodeStatus.UNCLEAR)
        }
        // TODO Update other node data such as marks etc.
    }

    fun addChildren(game: Game, sgfParent: SGFNode) {
        val fromNode = game.currentNode
        var passNode: PassNode? = null

        for (sgfChild in sgfParent.chldren) {
            game.rewindTo(fromNode) // Will do nothing for the first child in the list

            val gameNode = createGameNode(game, sgfChild)
            if (gameNode is MoveNode && fromNode is MoveNode && gameNode.color == fromNode.color) {
                // Add an extra Pass node (SGF does not have a concept of a pass node!
                // But only add ONE pass node, if there are many variations after the pass.
                if (passNode == null) {
                    passNode = PassNode()
                    game.addNode(passNode)
                }
                passNode.apply(game, null)
            }
            game.addNode(gameNode)
            gameNode.apply(game, null)
            addChildren(game, sgfChild)
        }
    }

    fun createGameNode(game: Game, sgfNode: SGFNode): GameNode {
        val white = sgfNode.getPropertyValue("W")
        if (white != null) {
            return MoveNode(toPoint(game.board, white), StoneColor.WHITE)
        }
        val black = sgfNode.getPropertyValue("B")
        if (black != null) {
            return MoveNode(toPoint(game.board, black), StoneColor.BLACK)
        }
        return SetupNode()
    }

    fun toStoneColor(str: String?): StoneColor? {
        if (str == "B") {
            return StoneColor.BLACK
        } else if (str == "W") {
            return StoneColor.WHITE
        }
        return null
    }

    fun toPoint(board: Board, str: String): Point {
        val x: Int = str[0].toLowerCase() - 'a'
        val y: Int = board.sizeY - (str[1].toLowerCase() - 'a') - 1
        return Point(x, y)
    }

    fun readTree(): SGFNode {

        val c = readCharSkippingWhiteSpace()
        if (c == '(') {
            val branch = SGFNode()
            readBranch(branch)
            return branch
            // Notes, a single SGF file can contain multiple games, but this code only reads the first.
        }
        throw IOException("Exptected '(', but found '$c'")
    }

    fun readBranch(branch: SGFNode) {

        var first = true
        var node = branch

        var c = readCharSkippingWhiteSpace()
        while (true) {
            if (c == ';') {
                if (first) {
                    first = false
                } else {
                    val newNode = SGFNode()
                    node.chldren.add(newNode)
                    node = newNode
                }
                readProperties(node)
            } else if (c == ')') {
                return
            } else if (c == '(') {
                readBranches(node)
            }
            c = readCharSkippingWhiteSpace()
        }
    }

    fun readBranches(parent: SGFNode) {
        while (true) {
            val newNode = SGFNode()
            parent.chldren.add(newNode)
            readBranch(newNode)
            val c = readCharSkippingWhiteSpace()
            if (c != '(') {
                unreadChar(c)
                return
            }
        }
    }

    fun readProperties(node: SGFNode) {

        while (true) {
            var ident = ""
            var c = readCharSkippingWhiteSpace()
            if (c?.isUpperCase() != true) {
                unreadChar(c)
                return
            }

            while (c?.isUpperCase() == true) {
                ident += c
                c = readChar()
            }
            while (c == '[') {
                val str = readPropertyValue()
                if (ident == "SZ") {
                    size = Integer.parseInt(str)
                }
                node.addProperty(ident, str)

                c = readCharSkippingWhiteSpace()
            }

            unreadChar(c)
            if (c?.isUpperCase() != true) {
                return
            }
        }
    }

    fun readPropertyValue(): String {
        var str = ""
        var escaped = false

        while (true) {
            var c = readChar()
            if (c == null) {
                throw IOException("End of file while reading property value")
            }
            if (escaped) {
                str += c
                escaped = false
            } else {
                if (c == '\\') {
                    escaped = true
                } else if (c == ']') {
                    return str
                } else {
                    str += c
                }
            }
        }
    }

    fun readCharSkippingWhiteSpace(): Char? {
        var symbol = readChar()
        while (symbol?.isWhitespace() == true) {
            symbol = readChar()
        }
        return symbol
    }

    fun unreadChar(c: Char?) {
        unread = c
    }

    fun readChar(): Char? {
        if (unread != null) {
            val tmp = unread
            unread = null
            return tmp
        }

        val read = reader.read(buffer)
        if (read < 0) {
            return null
        } else {
            return buffer[0]
        }
    }


    fun dumpTree(sgfNode: SGFNode) {

        fun dumpTree(indent: Int, sgfNode: SGFNode) {
            print(" ".repeat(indent * 4))
            for (child in sgfNode.chldren) {
                dumpTree(indent + 1, child)
            }
        }
        dumpTree(0, sgfNode)
    }
}

class SGFNode() {

    private val listProperties = mutableMapOf<String, MutableList<String>>()

    val chldren = mutableListOf<SGFNode>()

    fun addProperty(propertyName: String, value: String) {
        var list = listProperties.get(propertyName)
        if (list == null) {
            list = mutableListOf(value)
            listProperties.put(propertyName, list)
        } else {
            list.add(value)
        }
    }

    fun hasProperty(propertyName: String): Boolean {
        return listProperties.get(propertyName) != null
    }

    fun getPropertyValue(propertyName: String): String? {
        val list = listProperties.get(propertyName)
        if (list == null) {
            return null
        } else {
            return list[0]
        }
    }

    fun getPropertyValues(propertyName: String): List<String>? {
        return listProperties.get(propertyName)
    }

    override fun toString(): String {
        val builder = StringBuilder()

        builder.append("Node ")

        val black = getPropertyValue("B")
        val white = getPropertyValue("W")

        if (black != null) {
            builder.append("B @ $black ")
        } else if (white != null) {
            builder.append("W @ $white ")
        } else {
            builder.append("Setup Node $listProperties")
        }
        return builder.toString()
    }
}
