package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.kogo.gui.PlayingView
import uk.co.nickthecoder.kogo.model.Game
import uk.co.nickthecoder.kogo.model.GameVariation
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.Task
import uk.co.nickthecoder.paratask.parameters.*

abstract class AbstractGamePreferences : AbstractTask() {

    abstract val style: String

    val boardSizeP = ChoiceParameter("boardSize", value = 19)
            .choice("19", 19, "Standard (19x19)")
            .choice("17", 17, "(17x17)")
            .choice("15", 15, "(15x15)")
            .choice("13", 13, "Medium (13x13)")
            .choice("11", 11, "(11x11)")
            .choice("9", 9, "Small (9x9)")


    val handicapP = IntParameter("handicap", value = 0, range = 0..9)

    val fixedHandicapPointsP = BooleanParameter("fixedHandicapPoints", value = true)

    val komiP = DoubleParameter("komi", value = 0.0, minValue = -100.0, maxValue = 100.0)

    val timeLimitP = Preferences.timeLimitPreferences.createTimeLimitChoice()

    val allowUndoP = BooleanParameter("allowUndo", value = true)

    val gameVariationP = ChoiceParameter("gameVariation", value = GameVariation.NORMAL).enumChoices()

    fun createView(mainWindow: MainWindow): PlayingView {

        val game = Game(size = boardSizeP.value!!)
        game.metaData.komi = komiP.value!!
        game.metaData.handicap = handicapP.value!!
        game.metaData.fixedHandicaptPoints = fixedHandicapPointsP.value!!
        game.metaData.timeLimit = timeLimitP.value!!

        val view = PlayingView(mainWindow, game, allowUndoP.value!!)
        view.boardView.colorVariation = gameVariationP.value!!

        changePlayers(game)

        game.metaData.enteredBy = "Kogo"
        game.start()
        return view
    }

    abstract fun changePlayers(game: Game)

    abstract fun createLaunchTask(mainWindow: MainWindow): Task
}
