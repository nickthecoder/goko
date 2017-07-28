package uk.co.nickthecoder.kogo.model

import uk.co.nickthecoder.paratask.parameters.*

interface TimeLimit {

    fun key(): String
    fun status(): String
    fun details(): String
    fun copy(): TimeLimit
}

class NoTimeLimit : TimeLimit {

    override fun key() = "notimelimit"

    override fun details(): String {
        return "No Time Limit"
    }

    override fun status(): String {
        return "No Time Limit"
    }

    override fun copy() = this

    companion object {
        val instance = NoTimeLimit()
    }
}

val timeScales = mapOf<String, Double>("hours" to 60.0 * 60, "minutes" to 60.0, "seconds" to 1.0)

/**
 * All time periods are in stored in seconds.
 */
class TimedLimit(
        description: String,
        mainPeriod: Double,
        mainScale: Double,
        byoYomiPeriod: Double,
        byoYomiScale: Double,
        byoYomiMoves: Int = 1,
        overtimePeriod: Double = 0.0,
        overtimeScale: Double = 1.0,
        overtimePeriods: Int = 0) : TimeLimit {

    val descriptionP = StringParameter("description", value = description)
    val mainPeriodP = ScaledDoubleParameter("mainPeriod", value = ScaledValue(mainPeriod, mainScale), scales = timeScales)
    val byoYomiPeriodP = ScaledDoubleParameter("byoYomiPeriod", value = ScaledValue(byoYomiPeriod, byoYomiScale), scales = timeScales)
    val byoYomiMovesP = IntParameter("byoYomiMoves", value = byoYomiMoves)
    val overtimePeriodP = ScaledDoubleParameter("overtimePeriod", value = ScaledValue(overtimePeriod, overtimeScale), scales = timeScales)
    val overtimePeriodsP = IntParameter("overtimePeriods", value = overtimePeriods)

    val compound = CompoundParameter("timePeriod")

    var description by descriptionP

    var mainPeriod: Double
        get() = mainPeriodP.value.scaledValue
        set(v) {
            mainPeriodP.value.scaledValue = v
        }

    var byoYomiPeriod: Double
        get() = byoYomiPeriodP.value.scaledValue
        set(v) {
            byoYomiPeriodP.value.scaledValue = v
        }

    var byoYomiMoves by byoYomiMovesP


    var overtimePeriod: Double
        get() = overtimePeriodP.value.scaledValue
        set(v) {
            overtimePeriodP.value.scaledValue = v
        }

    var overtimePeriods by overtimePeriodsP

    init {
        compound.addParameters(descriptionP, mainPeriodP, byoYomiPeriodP, byoYomiMovesP, overtimePeriodP, overtimePeriodsP)
    }

    override fun key() = "$mainPeriod+$byoYomiPeriod/$byoYomiMoves+$overtimePeriod*$overtimePeriods"

    override fun details(): String {
        return main() + byoYomi() + overtime()
    }

    override fun status(): String {
        if (mainPeriod > 0) {
            return main() + overtime()
        }
        if (byoYomiMoves!! > 0 && byoYomiPeriod > 0) {
            return byoYomi() + overtime()
        }
        if (overtimePeriods!! > 0 && overtimePeriod > 0) {
            return overtime()
        }
        return "Time limit exceeded"
    }

    fun main() = "Main Time : ${humanTimePeriod(mainPeriod)}"

    fun byoYomi(prefix: String = "\n"): String {
        if (byoYomiPeriod > 0) {
            if (byoYomiMoves!! > 1) {
                return "${prefix}Byo-Yomi : $byoYomiMoves moves in ${humanTimePeriod(byoYomiPeriod)}"
            } else {
                return "${prefix}Byo-Yomi : ${humanTimePeriod(byoYomiPeriod)}"
            }
        } else {
            return ""
        }
    }

    fun overtime(prefix: String = "\n"): String {
        if (overtimePeriods!! > 0) {
            return "${prefix}Plus $overtimePeriods overtime periods"
        }
        return ""
    }

    override fun copy() = TimedLimit(
            description,
            mainPeriodP.value.value, mainPeriodP.value.scale,
            byoYomiPeriodP.value.value, byoYomiPeriodP.value.scale,
            byoYomiMoves!!,
            overtimePeriodP.value.value, overtimePeriodP.value.scale,
            overtimePeriods!!)

}

fun humanTimePeriod(period: Double): String = humanTimePeriod(period.toInt())

fun humanTimePeriod(period: Int): String {
    if (period > 60 * 60) {
        val hours = period / (60 * 60)
        val minutes = (period - hours * (60 * 60)) / 60
        return "$hours hours, $minutes minutes"
    }
    if (period > 60) {
        val minutes = period / 60
        val seconds = period - minutes * 60
        return "$minutes minutes $seconds seconds"
    }
    return "$period seconds"
}
