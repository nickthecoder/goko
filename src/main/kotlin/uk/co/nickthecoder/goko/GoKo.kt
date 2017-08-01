package uk.co.nickthecoder.goko

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.media.AudioClip
import javafx.stage.Stage
import uk.co.nickthecoder.goko.gui.MainWindow
import uk.co.nickthecoder.goko.preferences.Preferences
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.util.AutoExit
import java.io.InputStream

class GoKo : Application() {

    override fun start(stage: Stage) {
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
            val paratask = ParaTaskApp::class.java.getResource("paratask.css")
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
