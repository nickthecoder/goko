package uk.co.nickthecoder.kogo.preferences

import javafx.application.Platform
import uk.co.nickthecoder.kogo.LocalPlayer
import uk.co.nickthecoder.kogo.Player
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.GameListener
import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter

open class ChallengeMatchPreferences : AbstractTask(), GameListener {

    override val taskD = TaskDescription("challengeMatch", description =
    """Play aginst the Gnu Go robot.
Each time you play, the the handicap will change based on your previous results.
""")

    val boardSizeP = ChoiceParameter<Int>("boardSize", value = 19)
            .choice("19", 19, "Standard (19x19)")
            .choice("13", 13, "Medium (13x13)")
            .choice("9", 9, "Small (9x9)")

    val computerPlaysP = ChoiceParameter<StoneColor>("computerPlays", value = StoneColor.BLACK)
            .choice("BLACK", StoneColor.BLACK, "Black")
            .choice("WHITE", StoneColor.WHITE, " White")

    val computerLevelP = IntParameter("computerLevel", range = 1..20, value = 10)

    val handicapP = IntParameter("handicap", value = 0, range = 0..9)

    val promotionThresholdP = IntParameter(name = "promotionThreshold", value = 3,
            description = "The number of consecutive wins to rank up")

    val demotionThresholdP = IntParameter(name = "demotionThreshold", value = 3,
            description = "The number of consecutive loses for a demotion")

    val winsP = IntParameter("wins", value = 0)

    val losesP = IntParameter("loses", value = 0)

    val timeLimitP = Preferences.timeLimitPreferences.createTimeLimitChoice()

    init {
        taskD.addParameters(boardSizeP, computerPlaysP, computerLevelP, handicapP, promotionThresholdP, demotionThresholdP,
                winsP, losesP, timeLimitP
        )
    }

    override fun run() {
        Preferences.save()
    }

    override fun matchResult(game: Game, winner: Player?) {
        if (winner != null) {
            Platform.runLater {
                updateStats(winner is LocalPlayer)
            }
        }
    }

    fun updateStats(youWon: Boolean) {
        if (youWon) {
            winsP.value = winsP.value!! + 1
            losesP.value = 0
        } else if (!youWon) {
            losesP.value = losesP.value!! + 1
            winsP.value = 0
        }

        if (winsP.value!! >= promotionThresholdP.value!!) {
            promote()
        }
        if (losesP.value!! >= demotionThresholdP.value!!) {
            demote()
        }

        Preferences.save()
    }

    fun promote() {
        // TODO, Cheers and cork popping sounds
        winsP.value = 0

        if (computerPlaysP.value == StoneColor.BLACK) {
            handicapP.value = handicapP.value!! + 1
        } else {
            if (handicapP.value == 0) {
                computerPlaysP.value = StoneColor.BLACK
            } else {
                handicapP.value = handicapP.value!! - 1
            }
        }
    }

    fun demote() {
        // TODO, Boos and groans noise
        losesP.value = 0

        if (computerPlaysP.value == StoneColor.WHITE) {
            handicapP.value = handicapP.value!! + 1
        } else {
            if (handicapP.value == 0) {
                computerPlaysP.value = StoneColor.WHITE
            } else {
                handicapP.value = handicapP.value!! - 1
            }
        }
    }
}
