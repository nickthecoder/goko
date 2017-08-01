package uk.co.nickthecoder.goko.preferences

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskGroup
import uk.co.nickthecoder.paratask.parameters.*

/**
 */
class GamesPreferences : AbstractTask() {

    override val taskD = TaskDescription("games", description=
"""
Add you favourite types of games to the Home page.
""")

    val gamesP = MultipleParameter("games", label="") {
        val compound = CompoundParameter("game")

        val labelP = StringParameter("label", value = "")
        val taskP = TaskParameter("type", programmable = false, taskFactory = GameTypeFactory())
        compound.addParameters(labelP, taskP)

        compound
    }

    init {
        taskD.addParameters(gamesP)
    }

    override fun run() {
        Preferences.save()
    }
}

class GameTypeFactory : TaskFactory {

    override val topLevelTasks = listOf<Task>(ManVersesMachine(), TwoPlayerGame(), ChallengeMatch())

    override val creationStringToTask = topLevelTasks.associateBy({ it.creationString() }, { it })

    override val taskGroups = listOf<TaskGroup>()

}
