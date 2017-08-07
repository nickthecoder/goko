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
package uk.co.nickthecoder.goko

import javafx.application.Platform
import uk.co.nickthecoder.goko.model.Game

class ScoreEstimator(val game: Game) : GnuGoClient {

    private lateinit var callback: (String) -> Unit

    fun score(callback: (String) -> Unit) {
        // Run later to ensure that GnuGo has been updated first
        Platform.runLater {
            this.callback = callback
            game.createGnuGo().estimateScore(this)
        }
    }

    override fun estimateScoreResults(score: String) {
        Platform.runLater {
            callback(score.split(" ").firstOrNull() ?: "Unknown")
        }
    }

}
