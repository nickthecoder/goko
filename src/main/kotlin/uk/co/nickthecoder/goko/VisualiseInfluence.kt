package uk.co.nickthecoder.goko

import javafx.application.Platform
import uk.co.nickthecoder.goko.gui.MarksView
import uk.co.nickthecoder.goko.gui.SymbolMarkView
import uk.co.nickthecoder.goko.model.*

class VisualiseInfluence(val game: Game, val color: StoneColor, val type: String, val marksView: MarksView) : GnuGoClient {

    val node = game.currentNode

    fun visualise() {
        game.createGnuGo().influence(type, this, color)
    }

    override fun influenceResults(results: List<List<Double>>) {
        if (game.currentNode === node) {
            Platform.runLater {
                val max: Double = results.map { it.max() ?: 0.0 }.max() ?: 0.0
                for (y in 0..game.board.size - 1) {
                    for (x in 0..game.board.size - 1) {
                        val point = Point(x, y)
                        val symbol = InfluenceMark(point)
                        val mv = SymbolMarkView(symbol)
                        marksView.add(mv, color = color)
                        var opacity = results[x][y] / max
                        if (color == StoneColor.BLACK) {
                            opacity = opacity * 0.7
                        }
                        mv.opacity = opacity
                    }
                }
            }
        }
    }
}
