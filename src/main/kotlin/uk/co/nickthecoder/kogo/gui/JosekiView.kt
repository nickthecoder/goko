package uk.co.nickthecoder.kogo.gui

import javafx.scene.control.SplitPane
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.kogo.model.AlternateMark
import uk.co.nickthecoder.kogo.model.MoveNode
import uk.co.nickthecoder.kogo.model.SGFReader
import uk.co.nickthecoder.kogo.preferences.Preferences
import java.io.File

class JosekiView(mainWindow: MainWindow, val josekiDatabase: File)
    : AbstractGoView(mainWindow, SGFReader(josekiDatabase).read()) {

    override val title = "Joseki"

    private val split = SplitPane()

    private val rightPane = BorderPane()

    private val boardView = BoardView(game)

    private val commentsView = CommentsView(game, true, Preferences.josekiPreferences)

    override fun build() {
        super.build()
        boardView.build()

        with(rightPane) {
            center = commentsView.node
        }

        whole.center = split

        with(split) {
            items.add(boardView.node)
            items.add(rightPane)
            split.dividers[0].position = 0.7
        }
        commentsView.build()

        toolBar.items.addAll(passB, restartB, backB, forwardB)

        game.root.apply(game)
    }

    override fun tidyUp() {
        super.tidyUp()
        game.tidyUp()
        boardView.tidyUp()
        commentsView.tidyUp()
    }

    override fun moved() {
        super.moved()

        val currentNode = game.currentNode

        for (child in currentNode.children) {
            if (child is MoveNode) {
                if (!currentNode.hasMarkAt(child.point)) {
                    val mark = AlternateMark(child.point)
                    game.addMark(mark)
                }
            }
        }
    }

}
