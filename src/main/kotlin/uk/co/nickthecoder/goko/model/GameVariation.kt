package uk.co.nickthecoder.goko.model

import uk.co.nickthecoder.paratask.util.Labelled

enum class GameVariation(override val label: String) : Labelled {
    NORMAL("Normal"),
    ONE_COLOR_GO("One Color Go"),
    TWO_COLOR_ONE_COLOR_GO("Two Color One Color Go")
}
