package uk.co.nickthecoder.kogo

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.stage.Stage
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.paratask.ParaTaskApp
import java.io.InputStream
import java.net.URL

class KoGo : Application() {

    override fun start(stage: Stage) {
        MainWindow(stage).show()
    }

    companion object {


        private val imageMap = mutableMapOf<String, Image?>()

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
    }

}
