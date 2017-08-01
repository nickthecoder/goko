package uk.co.nickthecoder.goko.gui

import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import uk.co.nickthecoder.goko.GoKo
import uk.co.nickthecoder.paratask.gui.ApplicationAction

class GoKoAction(
        name: String,
        keyCode: KeyCode?,
        shift: Boolean? = false,
        control: Boolean? = false,
        alt: Boolean? = false,
        meta: Boolean? = false,
        shortcut: Boolean? = false,
        tooltip: String? = null,
        label: String? = null) : ApplicationAction(name, keyCode, shift, control, alt, meta, shortcut, tooltip, label) {

    override val image: Image? = GoKo.imageResource("buttons/$name.png")

    init {
        GoKoActions.add(this)
    }

}
