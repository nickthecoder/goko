package uk.co.nickthecoder.kogo.model

interface TimeLimit {
    fun key(): String
    fun status(): String
    fun details(): String
    fun duplicate(): TimeLimit
}

class NoTimeLimit : TimeLimit {

    override fun key() = "notimelimit"

    override fun details(): String {
        return "No Time Limit"
    }

    override fun status(): String {
        return "No Time Limit"
    }

    override fun duplicate() = this

    companion object {
        val instance = NoTimeLimit()
    }
}

/**
 * All time periods are in stored in seconds.
 */
data class TimedLimit(
        var mainPeriod: Double = 10.0 * 60,
        var byoYomiPeriod: Double = 10.0 * 60,
        var byoYomiMoves: Int = 25,
        var overtimePeriod: Double = 0.0,
        var overtimePeriods: Int = 0) : TimeLimit {

    override fun key() = "$mainPeriod+$byoYomiPeriod/$byoYomiMoves+$overtimePeriod*$overtimePeriods"

    override fun details(): String {
        return main() + byoYomi() + overtime()
    }

    override fun status(): String {
        if (mainPeriod > 0) {
            return main() + overtime()
        }
        if (byoYomiMoves > 0 && byoYomiPeriod > 0) {
            return byoYomi() + overtime()
        }
        if (overtimePeriods > 0 && overtimePeriod > 0) {
            return overtime()
        }
        return "Time limit exceeded"
    }

    fun main() = "Main Time : ${humanTimePeriod(mainPeriod)}"

    fun byoYomi(): String {
        if (byoYomiPeriod > 0) {
            if (byoYomiMoves > 0) {
                return "\nByo-Yomi : $byoYomiMoves in ${humanTimePeriod(byoYomiPeriod)}"
            } else {
                return "\nByo-Yomi : ${humanTimePeriod(byoYomiPeriod)}${overtime()}"
            }
        } else {
            return ""
        }
    }

    fun overtime(): String {
        if (overtimePeriods > 0) {
            return "\nPlus $overtimePeriods overtime periods"
        }
        return ""
    }

    override fun duplicate() = copy()

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
