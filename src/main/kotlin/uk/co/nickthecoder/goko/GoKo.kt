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
package uk.co.nickthecoder.goko

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.media.AudioClip
import javafx.stage.Stage
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.preferences.Preferences
import uk.co.nickthecoder.paratask.ParaTask
import uk.co.nickthecoder.paratask.parameters.FileParameter
import uk.co.nickthecoder.paratask.util.AutoExit
import java.io.InputStream

class GoKo : Application() {

    override fun start(stage: Stage) {
        FileParameter.showDragIcon = false
        FileParameter.showOpenButton = true
        AutoExit.disable()
        MainWindow(stage).show()
    }

    companion object {

        private val imageMap = mutableMapOf<String, Image?>()

        private val audioClips = mutableMapOf<String, AudioClip?>()

        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(GoKo::class.java, * args)
        }

        fun style(scene: Scene) {
            val goko = GoKo::class.java.getResource("goko.css")
            val paratask = ParaTask::class.java.getResource("paratask.css")
            scene.stylesheets.add(goko.toExternalForm())
            scene.stylesheets.add(paratask.toExternalForm())
        }

        fun resource(name: String): InputStream? {
            return GoKo::class.java.getResourceAsStream(name)
        }

        fun imageResource(name: String): Image? {
            val image = GoKo.imageMap[name]
            if (image == null) {
                val imageStream = GoKo::class.java.getResourceAsStream(name)
                val newImage = if (imageStream == null) null else Image(imageStream)
                GoKo.imageMap.put(name, newImage)
                return newImage
            }
            return image
        }

        fun audioClip(name: String): AudioClip? {
            var audioClip: AudioClip?
            val fullName = "audio/$name"
            if (audioClips.containsKey(fullName)) {
                return audioClips[name]
            }

            val urlString = GoKo::class.java.getResource(fullName).toString()
            try {
                audioClip = AudioClip(urlString)
            } catch (e: Exception) {
                e.printStackTrace()
                audioClip = null
            }
            audioClips[name] = audioClip
            return audioClip
        }

        fun stoneSound() {
            if (Preferences.basicPreferences.playSoundsP.value == true) {
                audioClip("tap.mp3")?.play()
            }
        }

    }

}
