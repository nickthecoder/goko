package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter

open class QuickGamePreferences : AbstractGamePreferences() {

    override val taskD = TaskDescription("quickGame", description = "Play a one-off game against the Gnu Go robot.")

    val computerPlaysP = ChoiceParameter<StoneColor>("computerPlays", value = StoneColor.BLACK)
            .choice("BLACK", StoneColor.BLACK, "Black")
            .choice("WHITE", StoneColor.WHITE, " White")

    val computerLevelP = IntParameter("computerLevel", range = 1..20, value = 10)

    init {
        taskD.addParameters(boardSizeP, computerPlaysP, computerLevelP, handicapP, fixedHandicapPointsP, komiP, timeLimitP, rulesP)
    }

    override fun run() {
        Preferences.save()
    }
}
