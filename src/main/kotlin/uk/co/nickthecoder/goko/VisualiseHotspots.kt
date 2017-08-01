package uk.co.nickthecoder.goko

import javafx.application.Platform
import uk.co.nickthecoder.goko.gui.MarksView
import uk.co.nickthecoder.goko.gui.SymbolMarkView
import uk.co.nickthecoder.goko.model.Game
import uk.co.nickthecoder.goko.model.InfluenceMark
import uk.co.nickthecoder.goko.model.Point
import uk.co.nickthecoder.goko.model.StoneColor

class VisualiseHotspots(val game: Game, val marksView: MarksView) : GnuGoClient {

    val node = game.currentNode

    var firstResults: List<List<Double>>? = null

    fun visualise() {
        // Run later to ensure that GnuGo has been updated first
        Platform.runLater {
            game.createGnuGo().influence("territory_value", this, StoneColor.BLACK)
            game.createGnuGo().influence("territory_value", this, StoneColor.WHITE)
        }
    }

    override fun influenceResults(results: List<List<Double>>) {

        if (firstResults == null) {
            println("Hotspots 1st half recieved")
            firstResults = results
        } else {
            println("Hotspots 2nd half recieved")
            Platform.runLater {
                showHotspots(firstResults!!, results)
            }
        }
    }

    fun showHotspots(first: List<List<Double>>, second: List<List<Double>>) {

        for (y in 0..game.board.size - 1) {
            for (x in 0..game.board.size - 1) {
                val point = Point(x, y)
                var diff = first[x][y] - second[x][y]
                var opacity: Double
                if (diff > 0) {
                    opacity = diff / 2
                } else {
                    opacity = -diff / 2
                }
                val symbol = InfluenceMark(point)
                val mv = SymbolMarkView(symbol)
                marksView.add(mv, "red")
                mv.opacity = opacity
            }
        }
    }
}
