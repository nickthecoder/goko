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

import javafx.application.Platform
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*
import uk.co.nickthecoder.paratask.parameters.compound.ScaledDouble
import uk.co.nickthecoder.paratask.parameters.compound.ScaledDoubleParameter
import java.util.*

class GameMetaData(val game: Game) : AbstractTask() {

    override val taskD = TaskDescription("editGameInformation")

    private val blackGroup = GroupParameter("blackPlayer")
    private val blackNameP = StringParameter("blackName", label = "Name", required = false)
    private val blackRankP = StringParameter("blackRank", label = "Rank", required = false)

    private val whiteGroup = GroupParameter("whitePlayer")
    private val whiteNameP = StringParameter("whiteName", label = "Name", required = false)
    private val whiteRankP = StringParameter("whiteRank", label = "Rank", required = false)

    private val resultP = StringParameter(name = "result", required = false)
    private val handicapP = IntParameter(name = "handicap", required = false)
    private val komiP = DoubleParameter(name = "komi", required = false)
    private val mainTimeP = ScaledDoubleParameter(name = "mainTime", value = ScaledDouble(0.0, 60.0, timeScales))
    private val overtimeP = StringParameter(name = "overtime", required = false)

    private val gameInfo = GroupParameter("gameInfo")
    private val datePlayedP = DateParameter(name = "datePlayed", required = false)
    private val eventP = StringParameter(name = "event", required = false)
    private val gameNameP = StringParameter(name = "gameName", required = false)
    private val placeP = StringParameter(name = "place", required = false)
    private val rulesP = StringParameter(name = "rules", required = false)
    private val gameCommentsP = StringParameter(name = "gameComments", required = false)

    private val authors = GroupParameter(name = "authors")
    private val copyrightP = StringParameter(name = "copyright", required = false)
    private val annotatorP = StringParameter(name = "annotator", required = false)
    private val enteredByP = StringParameter(name = "enteredBy", required = false)
    private val sourceP = StringParameter(name = "source", required = false)

    var blackName by blackNameP
    var blackRank by blackRankP
    var whiteName by whiteNameP
    var whiteRank by whiteRankP

    var result by resultP
    var mainTime by mainTimeP
    var overtime by overtimeP
    var datePlayed: Date?
        get() = datePlayedP.date
        set(v) {
            datePlayedP.date = v
        }

    var event by eventP
    var gameName by gameNameP
    var place by placeP
    var rules by rulesP
    var gameComments by gameCommentsP
    var handicap by handicapP
    var komi by komiP

    var copyright by copyrightP
    var annotator by annotatorP
    var enteredBy by enteredByP
    var source by sourceP

    var timeLimit: TimeLimit = NoTimeLimit()
        set(v) {
            field = v
            updateTimeLimit()
        }
    var fixedHandicaptPoints: Boolean = true // Not saved as an sgf property

    init {
        blackGroup.addParameters(blackNameP, blackRankP)
        whiteGroup.addParameters(whiteNameP, whiteRankP)
        gameInfo.addParameters(datePlayedP, eventP, gameNameP, placeP, rulesP, gameCommentsP)
        authors.addParameters(copyrightP, annotatorP, enteredByP, sourceP)

        taskD.addParameters(blackGroup, whiteGroup, resultP, handicapP, komiP, mainTimeP, overtimeP, gameInfo, authors)

        updateTimeLimit()
    }

    private fun updateTimeLimit() {
        val tl = timeLimit
        if (tl is TimedLimit) {
            mainTime = tl.mainPeriodP.value
            overtime = tl.byoYomi("") + tl.overtime(" ")
        } else {
            mainTime = ScaledDouble(0.0, 60.0, timeScales)
            overtime = ""
        }
    }

    override fun run() {
        Platform.runLater {
            game.updatedMetaData()
        }
    }

}
