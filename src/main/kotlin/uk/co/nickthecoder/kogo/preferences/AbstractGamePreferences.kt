package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.parameters.BooleanParameter
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.DoubleParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter

abstract class AbstractGamePreferences : AbstractTask() {

    val boardSizeP = ChoiceParameter<Int>("boardSize", value = 19)
            .choice("19", 19, "Standard (19x19)")
            .choice("13", 13, "Medium (13x13)")
            .choice("9", 9, "Small (9x9)")


    val handicapP = IntParameter("handicap", value = 0, range = 0..9)

    val fixedHandicapPointsP = BooleanParameter("fixedHandicapPoints", value = true)

    val komiP = DoubleParameter("komi", value = 0.0, minValue = -100.0, maxValue = 100.0)

    val timeLimitP = Preferences.timeLimitPreferences.createTimeLimitChoice()

    val rulesP = Preferences.createRulesChoice()


}
