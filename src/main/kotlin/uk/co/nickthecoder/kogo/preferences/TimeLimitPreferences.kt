package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.kogo.model.NoTimeLimit
import uk.co.nickthecoder.kogo.model.TimeLimit
import uk.co.nickthecoder.kogo.model.TimedLimit
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*

/**
 */
class TimeLimitPreferences : AbstractTask() {

    override val taskD = TaskDescription("timeLimits")

    val timeScales = mapOf<String, Double>("hours" to 60.0 * 60, "minutes" to 60.0, "seconds" to 1.0)

    val timeLimitsP = MultipleParameter<CompoundParameter>("timeLimits", minItems = 1) {
        val descriptionP = StringParameter("description")
        val mainPeriodP = ScaledDoubleParameter(name = "mainPeriod", value = 30.0, scales = timeScales, currentScale = 60.0)
        val byoYomiPeriodP = ScaledDoubleParameter(name = "byoYomiPeriod", value = 10.0, scales = timeScales, currentScale = 60.0)
        val byoYomiMoves = IntParameter(name = "byoYomiMoves", value = 25)
        val overtimePeriod = ScaledDoubleParameter(name = "overtimePeriod", value = 30.0, scales = timeScales, currentScale = 1.0)
        val overtimePeriods = IntParameter(name = "overtimePeriods", value = 3)

        val comp = CompoundParameter("timeLimit")

        comp.addParameters(descriptionP, mainPeriodP, byoYomiPeriodP, byoYomiMoves, overtimePeriod, overtimePeriods)
        comp
    }

    init {
        taskD.addParameters(timeLimitsP)

        addTimeLimit("30 minutes, plus 10 minutes byo-yomi per 25 moves", 30.0, 60.0, byoYomiPeriod = 10.0, byoYomiScale = 60.0, byoYomiMoves = 25)
        addTimeLimit("10 minutes, plus 30 seconds byo-yomi, 3 overtimes", 10.0, 60.0, byoYomiPeriod = 30.0, byoYomiScale = 1.0, overtimePeriods = 3)
        addTimeLimit("10 minutes, plus 30 seconds byo-yomi, no overtime", 10.0, 60.0, byoYomiPeriod = 30.0, byoYomiScale = 1.0)
    }


    fun addTimeLimit(
            description: String,
            mainPeriod: Double,
            mainScale: Double,
            byoYomiPeriod: Double,
            byoYomiScale: Double,
            byoYomiMoves: Int = 1,
            overtimePeriod: Double = byoYomiPeriod,
            overtimeScale: Double = byoYomiScale,
            overtimePeriods: Int = 0) {

        val compound = timeLimitsP.newValue() as CompoundParameter
        val descriptionP = compound.find("description") as StringParameter
        val mainPeriodP = compound.find("mainPeriod") as ScaledDoubleParameter
        val byoYomiPeriodP = compound.find("byoYomiPeriod") as ScaledDoubleParameter
        val byoYomiMovesP = compound.find("byoYomiMoves") as IntParameter
        val overtimePeriodP = compound.find("overtimePeriod") as ScaledDoubleParameter
        val overtimePeriodsP = compound.find("overtimePeriods") as IntParameter

        descriptionP.value = description
        mainPeriodP.value = mainPeriod
        mainPeriodP.currentScale = mainScale
        byoYomiPeriodP.value = byoYomiPeriod
        byoYomiPeriodP.currentScale = byoYomiScale
        byoYomiMovesP.value = byoYomiMoves
        overtimePeriodP.value = overtimePeriod
        overtimePeriodP.currentScale = overtimeScale
        overtimePeriodsP.value = overtimePeriods
    }

    fun createTimeLimitChoice(): ChoiceParameter<TimeLimit> {
        val noLimit = NoTimeLimit.instance
        val choiceP = ChoiceParameter<TimeLimit>("timeLimit", value = noLimit)

        updateTimeLimitChoice(choiceP)
        return choiceP
    }

    fun updateTimeLimitChoice(choiceP: ChoiceParameter<TimeLimit>) {
        val noLimit = NoTimeLimit.instance

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
                    mainPeriodP.scaledValue!!,
                    byoYomiPeriodP.scaledValue!!, byoYomiMovesP.value!!,
                    overtimePeriodP.scaledValue!!, overtimePeriodsP.value!!)

            choiceP.addChoice(timeLimit.key(), timeLimit, descriptionP.value)

        }
    }

    override fun run() {
        Preferences.save()
    }
}
