package uk.co.nickthecoder.goko

import javafx.application.Platform
import uk.co.nickthecoder.goko.gui.MarksView
import uk.co.nickthecoder.goko.gui.SymbolMarkView
import uk.co.nickthecoder.goko.model.Game
import uk.co.nickthecoder.goko.model.InfluenceMark
import uk.co.nickthecoder.goko.model.Point
import uk.co.nickthecoder.goko.model.StoneColor

class VisualiseTerritory(val game: Game, val color: StoneColor, val marksView: MarksView) : GnuGoClient {

    val node = game.currentNode

    fun visualise() {
        // Run later to ensure that GnuGo has been updated first
        Platform.runLater {
            game.createGnuGo().influence("territory_value", this, color)
        }
    }

    override fun influenceResults(results: List<List<Double>>) {

        if (game.currentNode === node) {
            Platform.runLater {
                for (y in 0..game.board.size - 1) {
                    for (x in 0..game.board.size - 1) {
                        val point = Point(x, y)
                        var score = -results[x][y]
                        var opacity: Double
                        val pointColor: StoneColor
                        if (score > 0) {
                            opacity = score * 0.7
                            pointColor = StoneColor.BLACK
                        } else {
                            opacity = -score
                            pointColor = StoneColor.WHITE
                        }
                        if (color == pointColor) {
                            val symbol = InfluenceMark(point)
                            val mv = SymbolMarkView(symbol)
                            marksView.add(mv, color = color)
                            mv.opacity = opacity
                        }
                    }
                }
            }
        }
    }
}
