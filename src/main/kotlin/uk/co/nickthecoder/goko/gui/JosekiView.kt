package uk.co.nickthecoder.goko.gui

import javafx.scene.control.SplitPane
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.goko.model.AlternateMark
import uk.co.nickthecoder.goko.model.GameNode
import uk.co.nickthecoder.goko.model.MoveNode
import uk.co.nickthecoder.goko.model.SGFReader
import uk.co.nickthecoder.goko.preferences.Preferences
import java.io.File

class JosekiView(mainWindow: MainWindow, josekiDatabase: File)
    : AbstractGoView(mainWindow, SGFReader(josekiDatabase).read()) {

    override val title = "Joseki"

    private val split = SplitPane()

    private val rightPane = BorderPane()

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

        // TODO Is this really needed?
        game.apply(game.root)
    }

    override fun tidyUp() {
        super.tidyUp()
        game.tidyUp()
        boardView.tidyUp()
        commentsView.tidyUp()
    }

    fun update() {
        println("Updating joseki view")
        val currentNode = game.currentNode
        boardView.branches.clear()

        for (child in currentNode.children) {
            println("Child move : $child")
            if (child is MoveNode) {
                if (!currentNode.hasMarkAt(child.point)) {
                    val mark = AlternateMark(child.point)
                    println("Added atlternate mark at ${child.point}")
                    boardView.branches.add(SymbolMarkView(mark))
                }
            }
        }
    }

    override fun madeMove(gameNode: GameNode) {
        println("madeMove joseki view")
        super.madeMove(gameNode)
        update()
    }

    override fun undoneMove(gameNode: GameNode) {
        println("UndoneMode joseki view")
        super.undoneMove(gameNode)
        update()
    }

}
