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

import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.TaskGroup
import uk.co.nickthecoder.paratask.parameters.*

/**
 */
class GamesPreferences : AbstractTask() {

    override val taskD = TaskDescription("games", description =
    """
Add your favourite types of games to the Home page.
""")

    val gamesP = MultipleParameter("games", label = "") {
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

    /**
     * If there are no games defined, create a few default ones.
     */
    fun ensureGamesExist() {
        if (gamesP.value.size == 0) {
            createGame("Man vs Machine", ManVersesMachine())
            createGame("Challenge", ChallengeMatch())
            createGame("Two Player", TwoPlayerGame())
        }
    }

    private fun createGame(label: String, task: Task) {
        val compound: CompoundParameter = gamesP.newValue().value
        (compound.find("label") as StringParameter).value = label
        (compound.find("type") as TaskParameter).value = task
    }

}

class GameTypeFactory : TaskFactory {

    override val topLevelTasks = listOf<Task>(ManVersesMachine(), TwoPlayerGame(), ChallengeMatch())

    override val creationStringToTask = topLevelTasks.associateBy({ it.creationString() }, { it })

    override val taskGroups = listOf<TaskGroup>()

}
