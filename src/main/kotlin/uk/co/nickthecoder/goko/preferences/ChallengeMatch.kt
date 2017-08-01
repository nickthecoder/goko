package uk.co.nickthecoder.goko.preferences

import javafx.application.Platform
import uk.co.nickthecoder.goko.GnuGoPlayer
import uk.co.nickthecoder.goko.LocalPlayer
import uk.co.nickthecoder.goko.Player
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.model.Game
import uk.co.nickthecoder.goko.model.GameListener
import uk.co.nickthecoder.goko.model.StoneColor
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.ChoiceParameter
import uk.co.nickthecoder.paratask.parameters.IntParameter

open class ChallengeMatch : AbstractGamePreferences(), GameListener {

    override val style = "challenge-match"

    final override val taskD = TaskDescription("challengeMatch", description =
    """Play against the Gnu Go robot.
Each time you play, the the handicap will change based on your previous results.
""")

    val computerLevelP = IntParameter("computerLevel", range = 1..20, value = 10)

    val computerPlaysP = ChoiceParameter("computerPlays", value = StoneColor.BLACK)
            .choice("BLACK", StoneColor.BLACK, "Black")
            .choice("WHITE", StoneColor.WHITE, " White")

    val promotionThresholdP = IntParameter(name = "promotionThreshold", value = 3,
            description = "The number of consecutive wins to rank up")

    val demotionThresholdP = IntParameter(name = "demotionThreshold", value = 3,
            description = "The number of consecutive loses for a demotion")

    val winsP = IntParameter("wins", value = 0)

    val losesP = IntParameter("loses", value = 0)

    init {
        taskD.addParameters(boardSizeP, computerLevelP, computerPlaysP, handicapP, fixedHandicapPointsP, komiP, timeLimitP,
                allowUndoP, promotionThresholdP, demotionThresholdP, winsP, losesP
        )
    }

    override fun run() {
        Preferences.save()
    }

    override fun changePlayers(game: Game) {
        val human = LocalPlayer(game, StoneColor.opposite(computerPlaysP.value!!), Preferences.yourName, Preferences.yourRank)
        human.timeRemaining = game.metaData.timeLimit.copy()

        val gnuGo = GnuGoPlayer(game, computerPlaysP.value!!)
        gnuGo.start()

        game.addPlayer(gnuGo)
        game.addPlayer(human)

        game.file = Preferences.gameFile("Challenge")
        // Listens for the end of the game to update number of wins/loses.
        val challengeMatch = (this as ChallengeMatchLauncher).parent
        game.listeners.add(challengeMatch)
    }

    override fun gameEnded(winner: Player?) {
        if (winner != null) {
            Platform.runLater {
                updateStats(winner is LocalPlayer)
            }
        }
    }

    fun updateStats(youWon: Boolean) {
        if (youWon) {
            winsP.value = winsP.value!! + 1
            losesP.value = 0
        } else if (!youWon) {
            losesP.value = losesP.value!! + 1
            winsP.value = 0
        }

        if (winsP.value!! >= promotionThresholdP.value!!) {
            promote()
        }
        if (losesP.value!! >= demotionThresholdP.value!!) {
            demote()
        }

        Preferences.save()
    }

    fun promote() {
        winsP.value = 0

        if (computerPlaysP.value == StoneColor.BLACK) {
            handicapP.value = handicapP.value!! + 1
            if (handicapP.value == 1) {
                // 1 stone handicap makes no sense, as we black already has the first move with a zero handicap.
                handicapP.value = 2
            }
        } else {
            if (handicapP.value == 0) {
                // When the computer plays white with no handicap, then promotion is for the computer to play black.
                computerPlaysP.value = StoneColor.BLACK
            } else {
                handicapP.value = handicapP.value!! - 1
                if (handicapP.value == 1) {
                    handicapP.value = 0
                }
            }
        }
    }

    fun demote() {
        losesP.value = 0

        if (computerPlaysP.value == StoneColor.WHITE) {
            handicapP.value = handicapP.value!! + 1
            if (handicapP.value == 1) {
                // 1 stone handicap makes no sense.
                handicapP.value = 2
            }
        } else {
            if (handicapP.value == 0) {
                computerPlaysP.value = StoneColor.WHITE
            } else {
                handicapP.value = handicapP.value!! - 1
                if (handicapP.value == 1) {
                    handicapP.value = 0
                }
            }
        }
    }

    override fun createLaunchTask(mainWindow: MainWindow): Task {
        return ChallengeMatchLauncher(mainWindow, this)
    }
}


class ChallengeMatchLauncher(val mainWindow: MainWindow, val parent: ChallengeMatch) : ChallengeMatch() {

    init {
        taskD.copyValuesFrom(parent.taskD)
    }

    override fun run() {
        mainWindow.changeView(createView(mainWindow))
    }

}
