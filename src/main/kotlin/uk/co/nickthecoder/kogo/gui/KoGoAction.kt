package uk.co.nickthecoder.kogo.gui

import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import uk.co.nickthecoder.kogo.KoGo
import uk.co.nickthecoder.paratask.ParaTaskApp
import uk.co.nickthecoder.paratask.project.AbstractAction

class KoGoAction(
        name: String,
        keyCode: KeyCode?,
        shift: Boolean? = false,
        control: Boolean? = false,
        alt: Boolean? = false,
        meta: Boolean? = false,
        shortcut: Boolean? = false,
        tooltip: String? = null,
        label: String? = null) : AbstractAction(name, keyCode, shift, control, alt, meta, shortcut, tooltip, label) {

    override val image: Image? = KoGo.imageResource("buttons/$name.png")

    init {
        KoGoActions.add(this)
    }

}
