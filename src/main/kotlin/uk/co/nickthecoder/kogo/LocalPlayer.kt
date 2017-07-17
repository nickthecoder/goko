package uk.co.nickthecoder.kogo

import uk.co.nickthecoder.kogo.model.StoneColor

class LocalPlayer(override val color: StoneColor, val name: String = "Human", override val rank: String = "") : Player {

    override val label
        get() = name

    override fun canClickToPlay() = true
}
