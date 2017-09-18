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
package uk.co.nickthecoder.goko.preferences

import uk.co.nickthecoder.goko.model.NoTimeLimit
import uk.co.nickthecoder.goko.model.TimeLimit
import uk.co.nickthecoder.goko.model.TimedLimit
import uk.co.nickthecoder.goko.model.timeScales
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.MultipleParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.parameters.compound.ScaledDouble
import uk.co.nickthecoder.paratask.parameters.compound.ScaledDoubleParameter

/**
 */
class TimeLimitPreferences : AbstractTask() {

    override val taskD = TaskDescription("timeLimits")

    val timeLimitsP = MultipleParameter("timeLimits", label = "") {
        TimedLimit("", ScaledDouble(30.0, 60.0, timeScales), ScaledDouble(10.0, 60.0, timeScales), 25, ScaledDouble(30.0, 1.0, timeScales), 3)
    }.asListDetail { it.descriptionText }

    init {
        taskD.addParameters(timeLimitsP)
    }

    fun addTimeLimit(timeLimit: TimedLimit) {
        if (contains(timeLimit)) {
            return
        }
        timeLimitsP.addValue(timeLimit)
    }

    private fun contains(timeLimit: TimedLimit): Boolean {
        for (compound in timeLimitsP.value) {

            val mainPeriodP = compound.find("mainPeriod") as ScaledDoubleParameter
            val byoYomiPeriodP = compound.find("byoYomiPeriod") as ScaledDoubleParameter
            val byoYomiMovesP = compound.find("byoYomiMoves") as IntParameter
            val overtimePeriodP = compound.find("overtimePeriod") as ScaledDoubleParameter
            val overtimePeriodsP = compound.find("overtimePeriods") as IntParameter

            //println("Main ${mainPeriodP.value.scaledValue} with ${timeLimit.mainPeriodP.value.scaledValue}")
            //println("ByoPeriod ${byoYomiPeriodP.value.scaledValue} with ${timeLimit.byoYomiPeriodP.value.scaledValue}")
            //println("ByoMoves ${byoYomiMovesP.value} with ${timeLimit.byoYomiMovesP.value}")
            //println("OvertimePeriod ${overtimePeriodP.value.scaledValue} with ${timeLimit.overtimePeriodP.value.scaledValue}")
            //println("Overtimes ${overtimePeriodsP.value} with ${timeLimit.overtimePeriodsP.value}")
            //println()
            if (mainPeriodP.value == timeLimit.mainPeriodP.value
                    && byoYomiPeriodP.value == timeLimit.byoYomiPeriodP.value
                    && byoYomiMovesP.value == timeLimit.byoYomiMovesP.value
                    && overtimePeriodP.value == timeLimit.overtimePeriodP.value
                    && overtimePeriodsP.value == timeLimit.overtimePeriodsP.value
                    ) {
                return true
            }
        }
        //println("Not found. Maintime of ${timeLimit.mainPeriodP.value.scaledValue}")
        return false
    }

    fun createTimeLimitChoice(): ChoiceParameter<TimeLimit> {
        val noLimit = NoTimeLimit.instance
        val choiceP = ChoiceParameter<TimeLimit>("timeLimit", value = noLimit)

        updateTimeLimitChoice(choiceP)
        return choiceP
    }

    fun updateTimeLimitChoice(choiceP: ChoiceParameter<TimeLimit>) {
        val noLimit = NoTimeLimit.instance

        val oldValue = choiceP.value
        choiceP.addChoice(noLimit.key(), noLimit, noLimit.details())

        val multi = Preferences.timeLimitPreferences.timeLimitsP
        for (compound in multi.value) {
            val descriptionP = compound.find("description") as StringParameter
            val mainPeriodP = compound.find("mainPeriod") as ScaledDoubleParameter
            val byoYomiPeriodP = compound.find("byoYomiPeriod") as ScaledDoubleParameter
            val byoYomiMovesP = compound.find("byoYomiMoves") as IntParameter
            val overtimePeriodP = compound.find("overtimePeriod") as ScaledDoubleParameter
            val overtimePeriodsP = compound.find("overtimePeriods") as IntParameter

            val timeLimit = TimedLimit(
                    descriptionP.value,
                    mainPeriodP.value,
                    byoYomiPeriodP.value,
                    byoYomiMovesP.value!!,
                    overtimePeriodP.value,
                    overtimePeriodsP.value!!)

            choiceP.addChoice(timeLimit.key(), timeLimit, descriptionP.value)

        }
        // The choices have been changed, so lets update the parameter's value. If an equivalent TimeLimit still exists, use that,
        // otherwise default to NoTimeLimit
        oldValue?.let { choiceP.stringValue = it.key() }
        if (choiceP.value == null) {
            choiceP.value = noLimit
        }
    }

    override fun run() {
        Preferences.save()
    }
}
