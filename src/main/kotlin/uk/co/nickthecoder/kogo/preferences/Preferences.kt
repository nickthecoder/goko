package uk.co.nickthecoder.kogo.preferences

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.kogo.model.TimedLimit
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.TaskParameter
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object Preferences {

    val basicPreferences = BasicPreferences()
    val yourName by basicPreferences.yourNameP
    val yourRank by basicPreferences.yourRankP
    val gamesDirectory by basicPreferences.gamesDirectoryP

    val timeLimitPreferences = TimeLimitPreferences()

    val gamesPreferences = GamesPreferences()

    val problemsPreferences = ProblemsPreferences()
    val problemsDirectory by problemsPreferences.directoryP
    val problemsShowContinuations by problemsPreferences.showBranchesP
    val problemsAutomaticOpponent by problemsPreferences.automaticOpponentP

    val josekiPreferences = JosekiPreferences()
    val josekiDirectionary by josekiPreferences.josekiDictionaryP

    val editGamePreferences = EditGamePreferences()
    val editGameShowMoveNumber by editGamePreferences.showMoveNumbersP


    val preferencesFile = homeDirectory.child(".config", "kogo", "preferences.json")

    val problemResultsDirectory = homeDirectory.child(".config", "kogo", "problems")


    val preferenceTasksMap = mutableMapOf<String, Task>()


    val listeners = mutableListOf<PreferencesListener>()

    private fun addPreferenceTask(task: Task) {
        preferenceTasksMap.put(task.taskD.name, task)
    }

    init {
        // NOTE. Time limits must come before any preferences that use time limits (such as quickGamePreferences).
        addPreferenceTask(basicPreferences)
        addPreferenceTask(timeLimitPreferences)
        addPreferenceTask(gamesPreferences)
        addPreferenceTask(problemsPreferences)
        addPreferenceTask(josekiPreferences)
        addPreferenceTask(editGamePreferences)

        if (preferencesFile.exists()) {
            load()
        }

    }

    /**
     * Creates a file path for a sgf file, based on the type of game and the current date and time.
     */
    fun gameFile(gameType: String): File {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val name = "$gameType ${format.format(Date().time)}.sgf"
        return File(gamesDirectory, name)
    }

    /**
     * Example json file :
     *
     * {
     *      preferences=[
     *          name="quickGamePreferences",
     *          parameters=[
     *              { name="foo", value="bar" },
     *              { name="baz", value=3 }
     *      ],
     *      [
     *          name="basicPreferences",
     *          parameters=[
     *              { name="foo", value="bar" },
     *              { name="baz", value=3 }
     *      ]
     * }
     */
    fun load() {

        val jroot = Json.parse(InputStreamReader(FileInputStream(preferencesFile))).asObject()
        val jpreferences = jroot.get("preferences").asArray()

        for (jtask1 in jpreferences) {
            val jtask = jtask1.asObject()
            val taskName = jtask.getString("name", "")

            val task = preferenceTasksMap[taskName]
            if (task != null) {
                val jparams = jtask.get("parameters").asArray()
                for (jparam1 in jparams) {
                    val jparam = jparam1.asObject()
                    val paramName = jparam.getString("name", "")
                    val stringValue = jparam.getString("value", "")

                    val parameter = task.taskD.root.find(paramName)
                    if (parameter != null && parameter is ValueParameter<*>) {
                        parameter.stringValue = stringValue
                    }
                }
            }
            if (taskName == "timeLimits") {
                updateTimeLimits()
            }
        }

    }

    fun save() {

        preferencesFile.parentFile.mkdirs()

        val jroot = JsonObject()
        val jpreferences = JsonArray()
        jroot.add("preferences", jpreferences)

        preferenceTasksMap.values.forEach { task ->
            val jtask = jsonTask(task)
            jpreferences.add(jtask)
        }

        BufferedWriter(OutputStreamWriter(FileOutputStream(preferencesFile))).use {
            jroot.writeTo(it, PrettyPrint.indentWithSpaces(4))
        }

        for (listener in listeners) {
            listener.preferencesChanged()
        }

        updateTimeLimits()
    }

    private fun updateTimeLimits() {

        timeLimitPreferences.addTimeLimit(TimedLimit("30 minutes, plus 10 minutes byo-yomi per 25 moves", 30.0, 60.0, byoYomiPeriod = 10.0, byoYomiScale = 60.0, byoYomiMoves = 25))
        timeLimitPreferences.addTimeLimit(TimedLimit("10 minutes, plus 30 seconds byo-yomi, 3 overtimes", 10.0, 60.0, byoYomiPeriod = 30.0, byoYomiScale = 1.0, overtimePeriod = 30.0, overtimePeriods = 3))
        timeLimitPreferences.addTimeLimit(TimedLimit("10 minutes, plus 30 seconds byo-yomi, no overtime", 10.0, 60.0, byoYomiPeriod = 30.0, byoYomiScale = 1.0))

        gamesPreferences.gamesP.value.forEach { compound ->
            val taskParameter = compound.find("type") as TaskParameter
            val task = taskParameter.value!! as AbstractGamePreferences
            timeLimitPreferences.updateTimeLimitChoice(task.timeLimitP)
        }
    }

    private fun jsonTask(task: Task): JsonObject {

        val jtask = JsonObject()

        jtask.add("name", task.taskD.name)
        val jparameters = JsonArray()
        jtask.add("parameters", jparameters)

        for (parameter in task.valueParameters()) {
            val jparameter = JsonObject()
            jparameter.set("name", parameter.name)
            jparameter.set("value", parameter.stringValue)
            jparameters.add(jparameter)
        }

        return jtask
    }
}

interface PreferencesListener {

    fun preferencesChanged()
}
