package uk.co.nickthecoder.kogo

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.media.AudioClip
import javafx.stage.Stage
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.util.AutoExit
import java.io.InputStream

class KoGo : Application() {

    override fun start(stage: Stage) {
        AutoExit.disable()
        MainWindow(stage).show()
    }

    companion object {


        private val imageMap = mutableMapOf<String, Image?>()

        private val audioClips = mutableMapOf<String, AudioClip?>()

        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(KoGo::class.java, * args)
        }

        fun style(scene: Scene) {
            val kogo = KoGo::class.java.getResource("kogo.css")
            val paratask = ParaTaskApp::class.java.getResource("paratask.css")
            scene.stylesheets.add(kogo.toExternalForm())
            scene.stylesheets.add(paratask.toExternalForm())
        }

        fun resource(name: String): InputStream? {
            return KoGo::class.java.getResourceAsStream(name)
        }

        fun imageResource(name: String): Image? {
            val image = KoGo.imageMap[name]
            if (image == null) {
                val imageStream = KoGo::class.java.getResourceAsStream(name)
                val newImage = if (imageStream == null) null else Image(imageStream)
                KoGo.imageMap.put(name, newImage)
                return newImage
            }
            return image
        }

        fun audioClip(name: String): AudioClip? {
            var audioClip: AudioClip?
            val fullName = "audio/${name}"
            if (audioClips.containsKey(fullName)) {
                return audioClips[name]
            }

            val urlString = KoGo::class.java.getResource(fullName).toString()
            try {
                audioClip = AudioClip(urlString)
            } catch (e: Exception) {
                e.printStackTrace()
                audioClip = null
            }
            audioClips[name] = audioClip
            return audioClip
        }
    }

}
