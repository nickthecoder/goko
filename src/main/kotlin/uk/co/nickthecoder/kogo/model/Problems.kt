package uk.co.nickthecoder.kogo.model

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonArray
import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.PrettyPrint
import javafx.scene.image.Image
import uk.co.nickthecoder.kogo.KoGo
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.paratask.util.FileLister
import java.io.*

object Problems {

    val pageSize = 100

    fun problemSets(): List<ProblemSet> {
        val result = mutableListOf<ProblemSet>()

        val topDirectory = Preferences.problemsDirectory!!

        val lister = FileLister(onlyFiles = false, depth = 5)
        val directories = lister.listFiles(topDirectory)
        directories.forEach { dir ->

            var page = 0
            do {
                val label = dir.path.substring(topDirectory.path.length + 1).replace(File.separatorChar, ' ')
                val problemSet = ProblemSet(dir, page, label)
                if (problemSet.problems.isNotEmpty()) {
                    result.add(problemSet)
                }
                page++

            } while (problemSet.problems.size == pageSize)
        }
        return result
    }
}

/**
 * A set of sgf files contained within a single directory.
 * If the directory contains too many files, then it will be split into multiple pages,
 * i.e. a single directory can have multiple ProblemSets.
 */
class ProblemSet(directory: File, page: Int = 0, label: String) {

    val problems = mutableListOf<Problem>()

    val label: String

    val listeners = mutableListOf<ProblemSetListener>()

    /*
     * Has the problem been solved?
     * The key is either the Filename (when each problem is within its own sgf file), or a the problemNumber + 1
     * (as a string) when the problems are all stored in a single sgf file.
     */
    var results: MutableMap<String, ProblemResult>? = null

    init {
        val lister = FileLister(extensions = listOf("sgf"))
        val list = lister.listFiles(directory)

        val count: Int

        if (list.size == 1) {
            // Lets assume that the sgf file contains multiple games
            val reader = SGFReader(list[0])
            val games = reader.readMultipleGames()
            count = games.size
            var previousProblem: ProblemWithinCompoundFile? = null
            for (i in (page * Problems.pageSize)..(-1 + Math.min((page + 1) * Problems.pageSize, games.size))) {
                val problem = ProblemWithinCompoundFile(this, list[0], i)
                problems.add(problem)
                previousProblem?.nextProblem = problem
                previousProblem = problem
            }

        } else {
            // Read only one game from each sgf file
            count = list.size
            for (i in (page * Problems.pageSize)..(-1 + Math.min((page + 1) * Problems.pageSize, list.size))) {
                problems.add(OneProblemPerFile(this, list[i]))
            }
        }

        this.label = if (count > Problems.pageSize) {
            "$label Part ${page + 1}"
        } else {
            label
        }
    }

    fun result(label: String): ProblemResult {
        if (results == null) {
            loadResults()
        }
        return results?.get(label) ?: ProblemResult.UNTRIED
    }

    fun setResult(label: String, result: ProblemResult) {
        if (results == null) {
            loadResults()
        }
        results?.put(label, result)
    }

    fun resultsFile(): File = File(Preferences.problemResultsDirectory, "$label.json")

    fun loadResults() {
        val results = mutableMapOf<String, ProblemResult>()

        val input = resultsFile()
        if (input.exists()) {
            val jroot = Json.parse(InputStreamReader(FileInputStream(input))).asObject()
            val jresults = jroot.get("results").asArray()

            for (jresult1 in jresults) {
                val jresult = jresult1.asObject()
                val label = jresult.getString("label", "")
                if (jresult.get("result") != null) {
                    val str = jresult.getString("result", "")
                    val result = ProblemResult.safeValueOf(str)
                    results.put(label, result)
                }
            }
        }
        this.results = results
    }

    fun saveResults() {
        results?.let { results ->

            val jroot = JsonObject()
            val jresults = JsonArray()
            jroot.add("results", jresults)

            results.forEach { key, value ->
                val jresult = JsonObject()
                jresult.add("label", key)
                jresult.add("result", value.toString())
                jresults.add(jresult)
            }

            val output = resultsFile()
            output.parentFile.mkdirs()

            BufferedWriter(OutputStreamWriter(FileOutputStream(output))).use {
                jroot.writeTo(it, PrettyPrint.indentWithSpaces(4))
            }
        }

        updated()
    }

    fun updated() {
        listeners.forEach { it.updated() }
    }
}

interface ProblemSetListener {
    fun updated()
}

abstract class Problem(val problemSet: ProblemSet) {

    abstract val label: String

    fun saveResult(result: ProblemResult) {
        problemSet.setResult(label, result)
        problemSet.saveResults()
    }

    abstract fun load(): Game

    abstract fun next(): Problem?

    fun getResult() = problemSet.result(label)
}

class ProblemWithinCompoundFile(problemSet: ProblemSet, val file: File, val problemNumber: Int) : Problem(problemSet) {

    override val label = (problemNumber + 1).toString()

    var nextProblem: Problem? = null


    override fun load(): Game {
        val reader = SGFReader(file)
        val games = reader.readMultipleGames()
        return games[problemNumber]
    }

    override fun next(): Problem? = nextProblem
}

class OneProblemPerFile(problemSet: ProblemSet, val file: File) : Problem(problemSet) {

    override val label = file.nameWithoutExtension

    override fun load(): Game = SGFReader(file).read()

    override fun next(): Problem? {
        var found = false
        for (problem in problemSet.problems) {
            if (found) {
                return problem
            }
            if (problem == this) {
                found = true
            }
        }
        return null
    }
}

enum class ProblemResult {
    UNTRIED, FAILED, SOLVED, UNCERTAIN;

    fun image(): Image? {
        return KoGo.imageResource("buttons/problem-${toString().toLowerCase()}.png")
    }

    fun style(): String {
        return toString().toLowerCase()
    }

    companion object {
        fun safeValueOf(str: String): ProblemResult {
            try {
                return valueOf(str)
            } catch (e: Exception) {
                return UNTRIED
            }
        }
    }
}
