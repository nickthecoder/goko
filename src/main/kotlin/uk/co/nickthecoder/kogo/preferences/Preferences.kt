package uk.co.nickthecoder.kogo.preferences

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.ValueParameter
import uk.co.nickthecoder.paratask.util.child
import uk.co.nickthecoder.paratask.util.homeDirectory
import java.io.*

object Preferences {

    val basicPreferences = BasicPreferences()

    val quickGamePreferences = QuickGamePreferences()

    val challengeMatchPreferences = ChallengeMatchPreferences()

    val problemsPreferences = ProblemsPreferences()

    val josekiPreferences = JosekiPreferences()

    val editGamePreferences = EditGamePreferences()


    val yourName by basicPreferences.yourNameP

    val yourRank by basicPreferences.yourRankP

    val gamesDirectory by basicPreferences.gamesDirectoryP


    val josekiDirectionary by josekiPreferences.josekiDictionaryP


    val editGameShowMoveNumber by editGamePreferences.showMoveNumbersP


    val problemsDirectory by problemsPreferences.directoryP

    val problemsShowContinuations by problemsPreferences.showContinuationsP

    val problemsAutomaticOpponent by problemsPreferences.automaticOpponentP


    val preferencesFile = homeDirectory.child(".config", "kogo", "preferences.json")

    val problemResultsDirectory = homeDirectory.child(".config", "kogo", "problems")


    val preferenceTasksMap = mutableMapOf<String, Task>()


    val listeners = mutableListOf<PreferencesListener>()

    private fun addPreferenceTask(task: Task) {
        preferenceTasksMap.put(task.taskD.name, task)
    }

    init {
        addPreferenceTask(basicPreferences)
        addPreferenceTask(quickGamePreferences)
        addPreferenceTask(challengeMatchPreferences)
        addPreferenceTask(problemsPreferences)
        addPreferenceTask(josekiPreferences)
        addPreferenceTask(editGamePreferences)

        if (preferencesFile.exists()) {
            load()
        }
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
            val task = preferenceTasksMap.get(taskName)
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
