package uk.co.nickthecoder.kogo

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import uk.co.nickthecoder.kogo.gui.MainWindow
import uk.co.nickthecoder.paratask.ParaTaskApp

class KoGo : Application() {

    override fun start(stage: Stage) {
        MainWindow(stage).show()
    }

    companion object {
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
    }
}
