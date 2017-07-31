package uk.co.nickthecoder.kogo.gui

import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.scene.media.AudioClip
import uk.co.nickthecoder.kogo.KoGo
import uk.co.nickthecoder.kogo.Player
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.kogo.preferences.Preferences
import java.util.*

/**
 */
class GameInfoView(val game: Game, val showTimeLimit: Boolean) : View, GameListener {

    val vBox = VBox()

    var countdown: Countdown? = null

    override val node = vBox

    val bPlayer = Label()
    val wPlayer = Label()

    val bTime = Label()
    val wTime = Label()

    val blackCapturesLabel = Label()
    val whiteCapturesLabel = Label()
    val handicapLabel = Label()
    val komiLabel = Label()
    val gameResultLabel = Label()
    val messageLabel = Label()

    init {
        game.listeners.add(this)
    }

    override fun build() {

        gameResultLabel.isWrapText = true

        with(vBox) {
            styleClass.add("game-info")
        }

        bPlayer.graphic = ImageView(KoGo.imageResource("buttons/mode-black.png"))
        wPlayer.graphic = ImageView(KoGo.imageResource("buttons/mode-white.png"))
        bPlayer.styleClass.add("heading")
        wPlayer.styleClass.add("heading")

        gameResultLabel.styleClass.add("game-result")

        messageLabel.isWrapText = true

        vBox.children.addAll(bPlayer, handicapLabel, blackCapturesLabel)
        if (showTimeLimit) {
            updateTimes()
            vBox.children.add(bTime)
        }
        vBox.children.addAll(wPlayer, komiLabel, whiteCapturesLabel)
        if (showTimeLimit) {
            vBox.children.add(wTime)
        }
        vBox.children.add(messageLabel)

        updateCaptures()
        updatedMetaData()
    }

    fun updateCaptures() {
        blackCapturesLabel.text = "Captured Stones : ${game.blackCaptures}"
        whiteCapturesLabel.text = "Captured Stones : ${game.whiteCaptures}"
    }

    fun updateTimes() {
        bTime.text = game.players[StoneColor.BLACK]!!.timeRemaining.details()
        wTime.text = game.players[StoneColor.WHITE]!!.timeRemaining.details()
    }

    fun update() {
        if (showTimeLimit) {
            countdown?.moved()
            updateTimes()
            val timeLimit = game.playerToMove.timeRemaining
            if (timeLimit is TimedLimit) {
                countdown = Countdown(timeLimit, game.playerToMove.color)
                countdown?.start()
            }
        }
        gameResultLabel.text = ""
        messageLabel.text = ""
        updateCaptures()
    }

    override fun madeMove(gameNode: GameNode) {
        update()
    }

    override fun undoneMove(gameNode: GameNode) {
        update()
    }

    override fun updatedMetaData() {
        bPlayer.text = game.metaData.blackName
        wPlayer.text = game.metaData.whiteName
        val handicap = if (game.metaData.handicap == null) {
            "Unknown"
        } else if (game.metaData.handicap == 0) {
            "None"
        } else {
            "${game.metaData.handicap} stones"
        }
        handicapLabel.text = "Handicap : ${handicap}"

        val komi = if (game.metaData.komi == null) {
            "Unknown"
        } else if (game.metaData.komi == 0.0) {
            "None"
        } else {
            game.metaData.komi.toString()
        }
        komiLabel.text = "Komi : $komi"
    }

    override fun gameEnded(winner: Player?) {
        vBox.children.add(gameResultLabel)
        gameResultLabel.text = "Game Result : ${game.metaData.result}"
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

    var lastPlayed: AudioClip? = null // Prevents the same clip being played again
    var audioClips = mutableListOf<AudioClip?>()

    init {
        for (i in 0..10) {
            if (Preferences.basicPreferences.playSoundsP.value == true) {
                audioClips.add(KoGo.audioClip("$i.mp3"))
            } else {
                audioClips.add(null)
            }
        }
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
                if (timeLeft < 0.0) {
                    timeLeft = 0.0
                }

                if (period == 0) {
                    timedLimit.mainPeriod = timeLeft
                } else if (period == 1) {
                    timedLimit.byoYomiPeriod = timeLeft
                } else {
                    timedLimit.overtimePeriod = timeLeft
                }

                if (timeLeft <= 0.0) {
                    lastPlayed = null
                    beginPeriod()
                    if (period < 0) {
                        Platform.runLater {
                            game.lostOnTime(game.players[color]!!)
                        }
                        return
                    }
                } else {
                    if (timeLeft < 11) {
                        val audioClip = audioClips[timeLeft.toInt()]
                        if (audioClip != lastPlayed) {
                            audioClip?.play()
                            lastPlayed = audioClip
                        }
                    } else {
                        lastPlayed = null
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

