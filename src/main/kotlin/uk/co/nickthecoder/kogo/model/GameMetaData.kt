package uk.co.nickthecoder.kogo.model

data class GameMetaData(
        val game: Game,
        var blackPlayer: String = "",
        var whitePler: String = "",
        var handicap: Int = 0,
        var matchResult: String = "",
        var timeLimit: TimeLimit = NoTimeLimit())
