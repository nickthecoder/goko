package uk.co.nickthecoder.goko.preferences

import uk.co.nickthecoder.goko.model.NoTimeLimit
import uk.co.nickthecoder.goko.model.TimeLimit
import uk.co.nickthecoder.goko.model.TimedLimit
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*

/**
 */
class TimeLimitPreferences : AbstractTask() {

    override val taskD = TaskDescription("timeLimits")

    val timeLimitsP = MultipleParameter("timeLimits", minItems = 1) {
        val timeLimit = TimedLimit("", 30.0, 60.0, 10.0, 60.0, 25, 30.0, 1.0, 3)
        timeLimit.compound
    }

    init {
        taskD.addParameters(timeLimitsP)
    }

    fun addTimeLimit(timeLimit: TimedLimit) {
        if (contains(timeLimit)) {
            return
        }
        timeLimitsP.addValue(timeLimit.compound)
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
            if (mainPeriodP.value.scaledValue == timeLimit.mainPeriodP.value.scaledValue
                    && byoYomiPeriodP.value.scaledValue == timeLimit.byoYomiPeriodP.value.scaledValue
                    && byoYomiMovesP.value == timeLimit.byoYomiMovesP.value
                    && overtimePeriodP.value.scaledValue == timeLimit.overtimePeriodP.value.scaledValue
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
                    mainPeriodP.value.value, mainPeriodP.value.scale,
                    byoYomiPeriodP.value.value, byoYomiPeriodP.value.scale,
                    byoYomiMovesP.value!!,
                    overtimePeriodP.value.value, overtimePeriodP.value.scale,
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
