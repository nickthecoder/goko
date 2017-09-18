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

import uk.co.nickthecoder.paratask.parameters.IntParameter
import uk.co.nickthecoder.paratask.parameters.MultipleGroupParameter
import uk.co.nickthecoder.paratask.parameters.StringParameter
import uk.co.nickthecoder.paratask.parameters.addParameters
import uk.co.nickthecoder.paratask.parameters.compound.ScaledDouble
import uk.co.nickthecoder.paratask.parameters.compound.ScaledDoubleParameter

interface TimeLimit {

    fun key(): String
    fun status(): String
    fun details(): String
    fun copyTimeLimit(): TimeLimit
}

class NoTimeLimit : TimeLimit {

    override fun key() = "notimelimit"

    override fun details(): String {
        return "No Time Limit"
    }

    override fun status(): String {
        return "No Time Limit"
    }

    override fun copyTimeLimit() = this

    companion object {
        val instance = NoTimeLimit()
    }
}

/**
 * All time periods are in stored in seconds.
 */
val timeScales = mapOf(60.0 * 60.0 to "hours", 60.0 to "minutes", 1.0 to "seconds")

class TimedLimit(
        description: String,
        mainPeriod: ScaledDouble,
        byoYomiPeriod: ScaledDouble,
        byoYomiMoves: Int = 1,
        overtimePeriod: ScaledDouble = ScaledDouble(0.0, 1.0, timeScales),
        overtimePeriods: Int = 0) : TimeLimit, MultipleGroupParameter("timeLimit") {


    val descriptionP = StringParameter("description", value = description)
    var descriptionText by descriptionP

    val mainPeriodP = ScaledDoubleParameter("mainPeriod", value = mainPeriod)
    var mainPeriod by mainPeriodP

    val byoYomiPeriodP = ScaledDoubleParameter("byoYomiPeriod", value = byoYomiPeriod)
    var byoYomiPeriod by byoYomiPeriodP

    val byoYomiMovesP = IntParameter("byoYomiMoves", value = byoYomiMoves)
    var byoYomiMoves by byoYomiMovesP

    val overtimePeriodP = ScaledDoubleParameter("overtimePeriod", value = overtimePeriod)
    var overtimePeriod by overtimePeriodP

    val overtimePeriodsP = IntParameter("overtimePeriods", value = overtimePeriods)
    var overtimePeriods by overtimePeriodsP


    init {
        addParameters(descriptionP, mainPeriodP, byoYomiPeriodP, byoYomiMovesP, overtimePeriodP, overtimePeriodsP)
    }

    override fun key() = "$mainPeriod+$byoYomiPeriod/$byoYomiMoves+$overtimePeriod*$overtimePeriods"

    override fun details(): String {
        return main() + byoYomi() + overtime()
    }

    override fun status(): String {
        if (mainPeriod.value > 0) {
            return main() + overtime()
        }
        if (byoYomiMoves!! > 0 && byoYomiPeriod.value > 0) {
            return byoYomi() + overtime()
        }
        if (overtimePeriods!! > 0 && overtimePeriod.value > 0) {
            return overtime()
        }
        return "Time limit exceeded"
    }

    fun main() = "Main Time : ${humanTimePeriod(mainPeriod.value)}"

    fun byoYomi(prefix: String = "\n"): String {
        if (byoYomiPeriod.value > 0) {
            if (byoYomiMoves!! > 1) {
                return "${prefix}Byo-Yomi : $byoYomiMoves moves in ${humanTimePeriod(byoYomiPeriod.value)}"
            } else {
                return "${prefix}Byo-Yomi : ${humanTimePeriod(byoYomiPeriod.value)}"
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

    override fun copyTimeLimit() = TimedLimit(
            descriptionText,
            mainPeriod,
            byoYomiPeriod,
            byoYomiMoves!!,
            overtimePeriod,
            overtimePeriods!!)

    override fun copy(): TimedLimit = copyTimeLimit()
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
