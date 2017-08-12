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

import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Reads a .sgf file.
 * See http://www.red-bean.com/sgf/index.html
 */

/*
Here's a list all sgf properties.
Those currently unsupported by this class are marked using (x)
Those partially supported are mark using (p)

Move Properties   B, KO (x), MN (x), W
Setup Properties 	AB, AE, AW, PL
Node Annotation Properties 	C, DM (p), GB (p), GW (p), HO (p), N, UC (p), V (x) (Ignored the "1" or "2" for NodeAnnotations
Move Annotation Properties 	BM, DO, IT, TE
Markup Properties 	AR (x), CR, DD (x), LB, LN (x), MA, SL (x), SQ, TR
Root Properties 	AP (x), CA (x), FF (x), GM (x), ST (x), SZ
Game Info Properties 	AN (x), BR (x), BT (x), CP (x), DT (x), EV (x), GN (x), GC (x), ON (x), OT (x), PB, PC (x), PW, RE (x), RO (x), RU (x), SO (x), TM (x), US (x), WR (x), WT (x)
Timing Properties 	BL (x), OB (x), OW (x), WL (x)
Miscellaneous Properties 	FG (x), PM (x), VW (x)
*/
class SGFReader {

    var file: File? = null

    var reader: Reader

    val buffer = charArrayOf(' ')

    var unread: Char? = null

    var size = 19

    constructor(input: InputStream) {
        this.reader = BufferedReader(InputStreamReader(input))
    }

    constructor(file: File) {
        this.file = file
        this.reader = file.bufferedReader()
    }

    /**
     * Reads an sgf file, returning a list of games, each game is represented as a tree of SGFNodes
     */
    fun readMultipleGames(): List<Game> {
        try {
            val result = mutableListOf<Game>()

            while (true) {
                val sgfRoot = readTree() ?: break
                val game = Game(size)
                updateRootNode(game, sgfRoot)
                addChildren(game, sgfRoot)

                //game.dumpTree()
                game.rewindTo(game.root)
                result.add(game)
            }
            if (result.size == 1 && result[0].root.children.filter { it is SetupNode }.count() > 1) {
                // There appears to be multiple games mashed together into one.
                // AFAIK, this does not follow the spec, but there are Go problems that are formatted like this
                // that I'd like to make use of, so here's a bodge to fix things.
                val compound = result[0]
                result.clear()

                val count = compound.root.children.size
                for (i in 0..count - 1) {
                    val node = compound.root.children[i]
                    if (node is SetupNode) {
                        val game = Game(compound.board.size)
                        game.root = node
                        game.root.parent = null
                        result.add(game)
                    }
                }

            }
            return result

        } finally {
            reader.close()
        }
    }

    /**
     * Reads an sgf file, returning a single Game (if the sgf file contains more than one game, then the first is
     * loaded, and the rest are silently ignored.
     */
    fun read(): Game {
        try {
            val sgfRoot = readTree() ?: throw IOException("No game data found")

            // dumpTree(sgfRoot)

            val game = Game(size)
            updateRootNode(game, sgfRoot)
            addChildren(game, sgfRoot)

            // game.dumpTree()

            game.file = file
            game.rewindTo(game.root)

            return game
        } finally {
            reader.close()
        }
    }

    private fun updateRootNode(game: Game, sgfNode: SGFNode) {

        playerToMoveSet = false

        game.metaData.blackName = sgfNode.getOptionalPropertyValue("PB")
        game.metaData.blackRank = sgfNode.getOptionalPropertyValue("BR")
        game.metaData.whiteName = sgfNode.getOptionalPropertyValue("PW")
        game.metaData.whiteRank = sgfNode.getOptionalPropertyValue("WR")

        game.metaData.result = sgfNode.getOptionalPropertyValue("RE")
        game.metaData.komi = sgfNode.getDoublePropertyValue("KM")
        game.metaData.mainTime.scaledValue = sgfNode.getDoublePropertyValue("TM") ?: 0.0
        game.metaData.overtime = sgfNode.getOptionalPropertyValue("OT")

        game.metaData.datePlayed = sgfNode.getDatePropertyValue("DT")
        game.metaData.event = sgfNode.getOptionalPropertyValue("EV")
        game.metaData.gameName = sgfNode.getOptionalPropertyValue("GN")
        game.metaData.place = sgfNode.getOptionalPropertyValue("PC")
        game.metaData.rules = sgfNode.getOptionalPropertyValue("RU")
        game.metaData.gameComments = sgfNode.getOptionalPropertyValue("GC")

        game.metaData.copyright = sgfNode.getOptionalPropertyValue("CP")
        game.metaData.annotator = sgfNode.getOptionalPropertyValue("AN")
        game.metaData.enteredBy = sgfNode.getOptionalPropertyValue("US")
        game.metaData.source = sgfNode.getOptionalPropertyValue("SO")

        game.metaData.komi = sgfNode.getPropertyValue("KM")?.toDouble() ?: 0.0

        updateNode(game, sgfNode)
    }

    private fun updateNode(game: Game, sgfNode: SGFNode) {
        val currentNode = game.currentNode

        // I've seen PL properties in non-root nodes, so let's put it here, rather than updateRootNode. Grr.
        val toPlay = toStoneColor(sgfNode.getPropertyValue("PL"))
        if (toPlay != null) {
            game.currentNode.colorToPlay = toPlay
            game.playerToMove = game.players[toPlay]!!
            playerToMoveSet = true
        }

        if (currentNode is SetupNode) {
            sgfNode.getPropertyValues("AW")?.forEach { str ->
                val point = toPoint(game.board, str)
                if (point != null) {
                    currentNode.addStone(game.board, point, StoneColor.WHITE)
                }
            }
            sgfNode.getPropertyValues("AB")?.forEach { str ->
                val point = toPoint(game.board, str)
                if (point != null) {
                    currentNode.addStone(game.board, point, StoneColor.BLACK)
                }
            }

            sgfNode.getPropertyValues("AE")?.forEach { str ->
                val point = toPoint(game.board, str)
                if (point != null) {
                    currentNode.removeStone(game.board, point)
                }
            }
        }


        sgfNode.getPropertyValue("C")?.let {
            currentNode.comment = it
        }
        sgfNode.getPropertyValue("N")?.let {
            currentNode.name = it
        }

        nodeAnnotation(sgfNode, currentNode, NodeAnnotation.GOOD_FOR_BLACK, "GB")
        nodeAnnotation(sgfNode, currentNode, NodeAnnotation.GOOD_FOR_WHITE, "GW")
        nodeAnnotation(sgfNode, currentNode, NodeAnnotation.EVEN, "DM")
        nodeAnnotation(sgfNode, currentNode, NodeAnnotation.HOTSPOT, "HO")
        nodeAnnotation(sgfNode, currentNode, NodeAnnotation.UNCLEAR, "UC")

        if (sgfNode.hasProperty("BM")) {
            currentNode.moveAnnotation = MoveAnnotation.BAD
        }
        if (sgfNode.hasProperty("DO")) {
            currentNode.moveAnnotation = MoveAnnotation.DOUBTFUL
        }
        if (sgfNode.hasProperty("IT")) {
            currentNode.moveAnnotation = MoveAnnotation.INTERESTING
        }
        if (sgfNode.hasProperty("TE")) {
            currentNode.moveAnnotation = MoveAnnotation.TESUJI
        }

        val labels = sgfNode.getPropertyValues("LB")
        labels?.forEach { str ->
            val point = toPoint(game.board, str.substring(0, 2))
            if (point != null) {
                val mark = LabelMark(point, str.substring(3))
                currentNode.addMark(mark)
            }
        }
        marks(game, sgfNode, "CR") { CircleMark(it) }
        marks(game, sgfNode, "MA") { CrossMark(it) }
        marks(game, sgfNode, "SQ") { SquareMark(it) }
        marks(game, sgfNode, "TR") { TriangleMark(it) }
        marks(game, sgfNode, "DD") { DimmedMark(it) }
    }

    private fun marks(game: Game, sgfNode: SGFNode, code: String, factory: (Point) -> Mark) {
        val marks = sgfNode.getPropertyValues(code)
        marks?.forEach { str ->
            val colon = str.indexOf(':')

            if (colon > 0 && str.length == 5) {
                // COMPRESSED points in a rectangle such as : DD[aa:bi][ca:ce]
                val from = toPoint(game.board, str.substring(0, 2))
                val to = toPoint(game.board, str.substring(3))
                if (from != null && to != null) {
                    val fromX = Math.min(from.x, to.x)
                    val toX = Math.max(from.x, to.x)
                    val fromY = Math.min(from.y, to.y)
                    val toY = Math.max(from.y, to.y)
                    for (x in fromX..toX) {
                        for (y in fromY..toY) {
                            val mark = factory(Point(x, y))
                            game.currentNode.addMark(mark)
                        }
                    }
                }
            } else {
                // Simple points such as : DD[aa][ca]
                val point = toPoint(game.board, str.substring(0, 2))
                if (point != null) {
                    val mark = factory(point)
                    game.currentNode.addMark(mark)
                }
            }
        }
    }

    private fun nodeAnnotation(sgfNode: SGFNode, node: GameNode, annotation: NodeAnnotation, property: String) {
        if (sgfNode.hasProperty(property)) {
            node.nodeAnnotation = annotation
            node.nodeAnnotationVery = sgfNode.getPropertyValue(property) == "2"
        }
    }

    var playerToMoveSet = false

    private fun addChildren(game: Game, sgfParent: SGFNode) {
        val fromNode = game.currentNode
        var passNode: PassNode? = null

        for (sgfChild in sgfParent.chldren) {
            game.rewindTo(fromNode) // Will do nothing for the first child in the list

            val gameNode = createGameNode(game, sgfChild)

            // If the "PL" property wasn't specified, then this is how we set playerToMove
            if (gameNode is MoveNode && fromNode === game.root && !playerToMoveSet) {
                playerToMoveSet = true
                game.playerToMove = game.players[gameNode.color]!!
            }

            if (gameNode is MoveNode && fromNode is MoveNode && gameNode.color == fromNode.color) {
                // The same player has moved again, so add an extra Pass node
                // But only add ONE pass node, if there are many variations after the pass.
                if (passNode == null) {
                    passNode = PassNode(game.playerToMove.color)
                    game.addNode(passNode, false)
                }
                game.apply(passNode)
            }
            //if (gameNode is SetupNode && game.currentNode is SetupNode) {
            //    // There are two setup nodes in a row, which seems pointless, so lets merge them into one node.
            //    updateNode(game, sgfChild)
            //    game.moveBack()
            //    game.moveForward()
            //} else {
            game.apply(game.addNode(gameNode, false))
            updateNode(game, sgfChild)
            //}
            addChildren(game, sgfChild)
        }
    }

    private fun createGameNode(game: Game, sgfNode: SGFNode): GameNode {
        val white = sgfNode.getPropertyValue("W")
        if (white != null) {
            val point = toPoint(game.board, white)
            if (point == null) {
                return PassNode(StoneColor.WHITE)
            } else {
                return MoveNode(point, StoneColor.WHITE)
            }
        }
        val black = sgfNode.getPropertyValue("B")
        if (black != null) {
            val point = toPoint(game.board, black)
            if (point == null) {
                return PassNode(StoneColor.BLACK)
            } else {
                return MoveNode(point, StoneColor.BLACK)
            }
        }
        return SetupNode(game.playerToMove.color)
    }

    private fun toStoneColor(str: String?): StoneColor? {
        // The spec says that only B and W are allowed, but I've seen 1 and 2 used. Grr.
        if (str == "B" || str == "1") {
            return StoneColor.BLACK
        } else if (str == "W" || str == "2") {
            return StoneColor.WHITE
        }
        return null
    }

    private fun toPoint(board: Board, str: String): Point? {
        if (str == "" || (str == "tt" && board.size <= 19)) {
            // A pass node - just return null and let the caller handle it.
            return null
        }
        try {
            val x: Int = str[0].toLowerCase() - 'a'
            val y: Int = board.size - (str[1].toLowerCase() - 'a') - 1
            return Point(x, y)
        } catch (e: Exception) {
            println("Invalid sgf point : '$str'")
            return null
        }
    }

    private fun readTree(): SGFNode? {

        // Skip ahead till the first "(;" is found. Some sgf files contain comments at the top, which is NOT
        // in the spec, but hey, what can you do!
        var c = readCharSkippingWhiteSpace()
        while (c != null) {
            if (c == '(') {
                c = readChar()
                if (c == ';') {
                    unreadChar(c)
                    val branch = SGFNode()
                    readBranch(branch)
                    return branch
                }
            }
            c = readCharSkippingWhiteSpace()
        }
        return null
    }

    private fun readBranch(branch: SGFNode) {

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

    private fun readBranches(parent: SGFNode) {
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

    private fun readProperties(node: SGFNode) {

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

    private fun readPropertyValue(): String {
        var str = ""
        var escaped = false

        while (true) {
            val c = readChar() ?: throw IOException("End of file while reading property value")
            if (escaped) {
                if ( c == '\n' ) {
                    // Do nothing. Weird, but the spec says that this is a "soft" newline, and should be replaced by "" <blank>
                } else {
                    str += c
                }
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

    private fun readCharSkippingWhiteSpace(): Char? {
        var symbol = readChar()
        while (symbol?.isWhitespace() == true) {
            symbol = readChar()
        }
        return symbol
    }

    private fun unreadChar(c: Char?) {
        unread = c
    }

    private fun readChar(): Char? {
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


    private fun dumpTree(sgfNode: SGFNode) {

        fun dumpTree(indent: Int, sgfNode2: SGFNode) {
            print(" ".repeat(indent * 4))
            for (child in sgfNode2.chldren) {
                dumpTree(indent + 1, child)
            }
        }
        dumpTree(0, sgfNode)
    }
}

class SGFNode {

    private val listProperties = mutableMapOf<String, MutableList<String>>()

    val chldren = mutableListOf<SGFNode>()

    fun addProperty(propertyName: String, value: String) {
        var list = listProperties[propertyName]
        if (list == null) {
            list = mutableListOf(value)
            listProperties.put(propertyName, list)
        } else {
            list.add(value)
        }
    }

    fun hasProperty(propertyName: String): Boolean {
        return listProperties[propertyName] != null
    }

    fun getOptionalPropertyValue(propertyName: String): String {
        return getPropertyValue(propertyName) ?: ""
    }

    fun getDoublePropertyValue(propertyName: String): Double? {
        return getPropertyValue(propertyName)?.toDouble()
    }

    fun getDatePropertyValue(propertyName: String): Date? {
        val str = getPropertyValue(propertyName)
        if (str == null) {
            return null
        } else {
            val format = SimpleDateFormat("yyyy-MM-dd")
            try {
                return format.parse(str)
            } catch (e: Exception) {
                println("Skipping badly formatted date : $str. Should be in YYYY-MM-DD format.")
                return null
            }
        }
    }

    fun getPropertyValue(propertyName: String): String? {
        val list = listProperties[propertyName]
        if (list == null) {
            return null
        } else {
            return list[0]
        }
    }

    fun getPropertyValues(propertyName: String): List<String>? {
        return listProperties[propertyName]
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
