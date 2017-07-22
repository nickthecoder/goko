package uk.co.nickthecoder.kogo.model

import java.io.BufferedWriter
import java.io.File
import java.io.OutputStream
import java.io.OutputStreamWriter

/**
 * Writes a game (or set of games) in sgf format.
 * See http://www.red-bean.com/sgf/index.html
 *
 */

/*
Here's a list all sgf properties.
Those currently unsupported by this class are marked using (x)
Those partially supported are mark using (p)


Move Properties 	B, KO (x), MN (x), W
Setup Properties 	AB, AE (x), AW, PL (x)
Node Annotation Properties 	C (x), DM (x), GB (x), GW (x), HO (x), N (x), UC (x), V (x)
Move Annotation Properties 	BM (x), DO (x), IT (x), TE (x)
Markup Properties 	AR (x), CR, DD (x), LB, LN (x), MA, SL (x), SQ, TR
Root Properties 	AP (x), CA (x), FF (x), GM (x), ST (x), SZ (x)
Game Info Properties 	AN(x), BR(x), BT(x), CP(x), DT(x), EV(x), GN(x), GC(x), ON(x), OT(x), PB(x), PC(x), PW(x), RE(x), RO(x), RU(x), SO(x), TM(x), US(x), WR(x), WT(x)
Timing Properties 	BL(x), OB(x), OW(x), WL(x)
Miscellaneous Properties 	FG(x), PM(x), VW(x)
 */
class SGFWriter {

    private val writer: BufferedWriter

    private lateinit var game: Game

    constructor(file: File) {
        game.file = file
        writer = file.bufferedWriter()
    }

    constructor(output: OutputStream) {
        writer = BufferedWriter(OutputStreamWriter(output))
    }

    fun write(game: Game) {
        try {
            writeSingleGame(game)
        } finally {
            writer.close()
        }
    }

    fun write(games: List<Game>) {
        for (game in games) {
            writeSingleGame(game)
        }
    }

    private fun writeSingleGame(game: Game) {
        this.game = game
        writer.write("(;GM[1]FF[4]")
        writer.write("\n")

        writeGameMetaData()

        writeNode(game.root)

        writer.write(")\n")
    }

    private fun writeGameMetaData() {
        // Players names, ranks, board size etc
    }

    private fun writeNode(node: GameNode) {

        writer.write(";")
        if (node is MoveNode) {
            writeMoveNode(node)
        } else if (node is PassNode) {
            writePassNode(node)
        } else if (node is SetupNode) {
            writeSetupNode(node)
        }
        writeGeneralNode(node)

        if (node.children.isEmpty()) {
            return
        }
        if (node.children.size == 1) {
            writeNode(node.children[0])
        } else {
            writer.write("\n")
            for (child in node.children) {
                writer.write("(")
                writeNode(child)
                writer.write(")")
            }
        }
    }

    private fun writeMoveNode(node: MoveNode) {
        val col = if (node.colorToPlay == StoneColor.WHITE) "W" else "B"
        val pt = fromPoint(node.point)
        writer.write("$col[$pt]")
    }

    private fun writePassNode(node: PassNode) {
        val col = if (node.colorToPlay == StoneColor.WHITE) "W" else "B"
        writer.write("$col[]")
    }

    private fun writeSetupNode(node: SetupNode) {
        writePoints("AW", node.addedStones.filter { (_, color) -> color == StoneColor.WHITE }.map { (point, _) -> point })
        writePoints("AB", node.addedStones.filter { (_, color) -> color == StoneColor.BLACK }.map { (point, _) -> point })
    }

    private fun writePoints(property: String, points: List<Point>) {
        if (points.isNotEmpty()) {
            writer.write(property)
            points.forEach {
                val pt = fromPoint(it)
                writer.write("$[$pt]")
            }
        }
    }

    private fun writeLabelledPoints(property: String, marks: List<LabelMark>) {
        if (marks.isNotEmpty()) {
            writer.write(property)
            marks.forEach { mark ->
                val pt = fromPoint(mark.point)
                writer.write("$[$pt:${mark.text}]")
            }
        }
    }

    private fun writeGeneralNode(node: GameNode) {
        writePoints("CR", node.marks.filter { it is CircleMark }.map { it.point })
        writePoints("SQ", node.marks.filter { it is SquareMark }.map { it.point })
        writePoints("TR", node.marks.filter { it is TriangleMark }.map { it.point })
        writePoints("MA", node.marks.filter { it is CrossMark }.map { it.point })
        writeLabelledPoints("LB", node.marks.filter { it is LabelMark }.map { it as LabelMark })
    }

    private fun fromPoint(point: Point): String {
        val x = 'a' + point.x
        val y = 'a' + (game.board.sizeY - 1 - point.y)

        return "$x$y"
    }
}
