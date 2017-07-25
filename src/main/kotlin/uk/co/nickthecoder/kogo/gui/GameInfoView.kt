package uk.co.nickthecoder.kogo.gui

import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import uk.co.nickthecoder.kogo.KoGo
import uk.co.nickthecoder.kogo.Player
import uk.co.nickthecoder.kogo.model.*
import java.util.*

/**
 */
class GameInfoView(val game: Game) : View, GameListener {

    val vBox = VBox()

    var countdown: Countdown? = null

    override val node = vBox

    val bTime = Label()
    val wTime = Label()

    val blackCapturesLabel = Label()
    val whiteCapturesLabel = Label()

    val gameResultLabel = Label()

    init {
        game.gameListeners.add(this)
    }

    override fun build() {

        with(vBox) {
            styleClass.add("game-info")
        }

        val bPlayer = Label(game.players[StoneColor.BLACK]?.label)
        val wPlayer = Label(game.players[StoneColor.WHITE]?.label)
        bPlayer.graphic = ImageView(KoGo.imageResource("buttons/mode-black.png"))
        wPlayer.graphic = ImageView(KoGo.imageResource("buttons/mode-white.png"))
        bPlayer.styleClass.add("heading")
        wPlayer.styleClass.add("heading")

        val handicap = if (game.metaData.handicap == 0) "None" else "${game.metaData.handicap} stones"
        val handicapLabel = Label("Handicap : ${handicap}")

        val komi = if (game.metaData.komi == 0.0) "None" else game.metaData.komi.toString()
        val komiLabel = Label("Komi : $komi")

        gameResultLabel.styleClass.add("game-result")
        vBox.children.addAll(bPlayer, handicapLabel, blackCapturesLabel, bTime, wPlayer, komiLabel, whiteCapturesLabel, wTime, gameResultLabel)

        updateTimes()
        updateCaptures()
    }

    fun updateCaptures() {
        blackCapturesLabel.text = "Captured Stones : ${game.blackCaptures}"
        whiteCapturesLabel.text = "Captured Stones : ${game.whiteCaptures}"
    }

    fun updateTimes() {
        bTime.text = game.players[StoneColor.BLACK]!!.timeRemaining.details()
        wTime.text = game.players[StoneColor.WHITE]!!.timeRemaining.details()
    }

    override fun moved() {
        countdown?.moved()
        updateTimes()
        val timeLimit = game.playerToMove.timeRemaining
        if (timeLimit is TimedLimit) {
            countdown = Countdown(timeLimit, game.playerToMove.color)
            countdown?.start()
        }
    }

    override fun gameEnded(winner: Player?) {
        gameResultLabel.text = "Game Result : ${game.metaData.gameResult}"
        stopCountdown()
    }

    fun stopCountdown() {
        countdown?.finished = true
        countdown = null
    }

    override fun tidyUp() {
        super.tidyUp()
        stopCountdown()
    }

    inner class Countdown(val timedLimit: TimedLimit, val color: StoneColor) : Runnable {

        var period: Int = 0
        var startTimeMillis = 0L
        var startPeriodSeconds: Double = 0.0
        var finished: Boolean = false

        fun start() {
            val thread = Thread(this, "countdown")
            thread.isDaemon = true
            beginPeriod()
            thread.start()
        }

        fun beginPeriod() {
            startTimeMillis = Date().time

            if (timedLimit.mainPeriod > 0) {
                period = 0
                startPeriodSeconds = timedLimit.mainPeriod
            } else if (timedLimit.byoYomiPeriod > 0) {
                period = 1
                startPeriodSeconds = timedLimit.byoYomiPeriod
            } else if (timedLimit.overtimePeriod > 0) {
                period = 2
                startPeriodSeconds = timedLimit.overtimePeriod
            } else {
                period = -1
            }

        }

        override fun run() {
            while (!finished) {

                val now = Date().time
                val ellapsedMillis = now - startTimeMillis
                var timeLeft = startPeriodSeconds - (ellapsedMillis / 1000.0)
                if (timeLeft < 0) {
                    timeLeft = 0.0
                }
                if (period == 0) {
                    timedLimit.mainPeriod = timeLeft
                } else if (period == 1) {
                    timedLimit.byoYomiPeriod = timeLeft
                } else {
                    timedLimit.overtimePeriod = timeLeft
                }
                if (timeLeft == 0.0) {
                    beginPeriod()
                    if (period < 0) {
                        game.lostOnTime(game.players[color]!!)
                        return
                    }
                }

                Platform.runLater { updateTimes() }
                Thread.sleep(1000)
            }
        }

        fun moved() {
            val gameTimeLimit = game.metaData.timeLimit as TimedLimit

            if (period == 1) {
                timedLimit.byoYomiMoves = timedLimit.byoYomiMoves!! - 1
                if (timedLimit.byoYomiMoves == 0) {
                    timedLimit.byoYomiPeriod = gameTimeLimit.byoYomiPeriod
                    timedLimit.byoYomiMoves = gameTimeLimit.byoYomiMoves
                }
            } else if (period == 2) {
                timedLimit.overtimePeriods = timedLimit.overtimePeriods!! - 1
            }

            finished = true
        }

    }
}

