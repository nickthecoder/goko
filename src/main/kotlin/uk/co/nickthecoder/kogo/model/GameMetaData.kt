package uk.co.nickthecoder.kogo.model

data class GameMetaData(
        val game: Game,
        var blackPlayer: String = "",
        var whitePlayer: String = "",
        var handicap: Int = 0,
        var gameResult: String = "",
        var komi: Double = 0.0,
        var japaneseRules : Boolean = true,
        var timeLimit: TimeLimit = NoTimeLimit())
