package uk.co.nickthecoder.kogo.preferences

import uk.co.nickthecoder.kogo.model.GameListener
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription
import uk.co.nickthecoder.paratask.parameters.*

open class TwoPlayerGamePreferences : AbstractGamePreferences(), GameListener {

    override val taskD = TaskDescription("twoPlayerGame")

    val blackPlayerP = StringParameter("blackName", label = "Black's Name", required = false, value = Preferences.yourName)

    val whitePlayerP = StringParameter("whiteName", label = "Whites's Name", required = false, value = "Mister White")
    
    init {
        taskD.addParameters(boardSizeP, blackPlayerP, whitePlayerP, handicapP, fixedHandicapPointsP, komiP, timeLimitP, rulesP)
    }

    override fun run() {
        Preferences.save()
    }
}
