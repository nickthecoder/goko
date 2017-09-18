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
package uk.co.nickthecoder.goko.gui

import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.VBox
import javafx.scene.media.AudioClip
import uk.co.nickthecoder.goko.GoKo
import uk.co.nickthecoder.goko.Player
import uk.co.nickthecoder.goko.model.*
import uk.co.nickthecoder.goko.preferences.Preferences
import java.util.*

/**
 */
class GameInfoView(val game: Game, val showTimeLimit: Boolean) : View, GameListener {

    private val vBox = VBox()

    private var countdown: Countdown? = null

    override val node = vBox

    private val bPlayer = Label()
    private val wPlayer = Label()

    private val bTime = Label()
    private val wTime = Label()

    private val blackCapturesLabel = Label()
    private val whiteCapturesLabel = Label()
    private val handicapLabel = Label()
    private val komiLabel = Label()

    /**
     * Note, this class does NOT listen for gameMessage events directly, the PlayingView/EditGameView etc does,
     * and may forward the message here. This is because EditGameView has two parts, both of which are capable of
     * displaying messages, and only ONE of them should.
     */
    private val messageLabel = Label()

    init {
        game.listeners.add(this)
    }

    override fun build() {

        with(vBox) {
            styleClass.add("game-info")
        }

        bPlayer.graphic = ImageView(GoKo.imageResource("buttons/mode-black.png"))
        wPlayer.graphic = ImageView(GoKo.imageResource("buttons/mode-white.png"))
        bPlayer.styleClass.add("heading")
        wPlayer.styleClass.add("heading")

        messageLabel.styleClass.add("message")
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
        handicapLabel.text = "Handicap : $handicap"

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
        message("Game Result : ${game.metaData.result}")
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
                audioClips.add(GoKo.audioClip("$i.mp3"))
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

            if (timedLimit.mainPeriod.value > 0) {
                period = 0
                startPeriodSeconds = timedLimit.mainPeriod.value
            } else if (timedLimit.byoYomiPeriod.value > 0) {
                period = 1
                startPeriodSeconds = timedLimit.byoYomiPeriod.value
            } else if (timedLimit.overtimePeriod.value > 0) {
                period = 2
                startPeriodSeconds = timedLimit.overtimePeriod.value
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
                    timedLimit.mainPeriod.value = timeLeft
                } else if (period == 1) {
                    timedLimit.byoYomiPeriod.value = timeLeft
                } else {
                    timedLimit.overtimePeriod.value = timeLeft
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

    fun message(message: String) {
        messageLabel.text = message
    }
}
