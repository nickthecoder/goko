package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*

class GameMetaData() : AbstractTask() {

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
    private val mainTimeP = ScaledDoubleParameter(name = "mainTime", scales = timeScales, value = ScaledValue(0.0, 60.0))
    private val overtimeP = StringParameter(name = "overtime", required = false)

    private val gameInfo = GroupParameter("gameInfo")
    private val datePlayedP = DateParameter(name = "datePlayed", required = false)
    private val gameNameP = StringParameter(name = "gameName", required = false)
    private val eventP = StringParameter(name = "event", required = false)
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
    var gameResult by resultP
    var mainTime by mainTimeP
    var overtime by overtimeP
    var datePlayed by datePlayedP
    var event by eventP
    var place by placeP
    var rules by rulesP
    var gameComments by gameCommentsP
    var handicap by handicapP
    var komi by komiP

    var timeLimit: TimeLimit = NoTimeLimit()
        set(v) {
            field = v
            updateTimeLimit()
        }
    var fixedHandicaptPoints: Boolean = true // Not saved as an sgf property
    var japaneseRules: Boolean = true // Here for GnuGo only

    init {
        blackGroup.addParameters(blackNameP, blackRankP)
        whiteGroup.addParameters(whiteNameP, whiteRankP)
        gameInfo.addParameters(datePlayedP, gameNameP, eventP, placeP, rulesP, gameCommentsP)
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
            mainTime = ScaledValue(0.0, 60.0)
            overtime = ""
        }
    }

    override fun run() {
        // Do nothing! We only care about gathering the information, we don't need to do anything with it.
    }

}
