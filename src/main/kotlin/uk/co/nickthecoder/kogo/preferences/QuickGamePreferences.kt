package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter

open class QuickGamePreferences : AbstractTask() {

    override val taskD = TaskDescription("quickGame", description = "Play a one-off game against the Gnu Go robot.")

    val boardSizeP = ChoiceParameter<Int>("boardSize", value = 19)
            .choice("19", 19, "Standard (19x19)")
            .choice("13", 13, "Medium (13x13)")
            .choice("9", 9, "Small (9x9)")

    val computerPlaysP = ChoiceParameter<StoneColor>("computerPlays", value = StoneColor.BLACK)
            .choice("BLACK", StoneColor.BLACK, "Black")
            .choice("WHITE", StoneColor.WHITE, " White")

    val computerLevelP = IntParameter("computerLevel", range = 1..20, value = 10)

    val handicapP = IntParameter("handicap", value = 0, range = 0..9)

    val komiP = DoubleParameter("komi", value = 0.0, minValue = -100.0, maxValue = 100.0)

    val timeLimitP = Preferences.timeLimitPreferences.createTimeLimitChoice()

    val rulesP = Preferences.createRulesChoice()

    init {
        taskD.addParameters(boardSizeP, computerPlaysP, computerLevelP, handicapP, komiP, timeLimitP, rulesP)
    }

    override fun run() {
        Preferences.save()
    }
}
