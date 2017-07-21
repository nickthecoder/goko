package uk.co.nickthecoder.kogo.gui

import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.kogo.model.*
import java.io.File

class JosekiView(mainWindow: MainWindow, val josekiDatabase: File)
    : TopLevelView(mainWindow), GameListener {

    val game = SGFReader(josekiDatabase).read()

    override val title = "Joseki"

    val board: Board
        get() = game.board

    private val whole = BorderPane()

    private val toolBar = ToolBar()

    private val split = SplitPane()

    private val rightPane = BorderPane()

    private val boardView = BoardView(game)

    private val commentsView = CommentsView(game)


    override val node = whole

    init {
        game.gameListeners.add(this)
    }

    override fun build(): View {
        boardView.build()

        with(rightPane) {
            center = commentsView.node
        }

        whole.top = toolBar
        whole.center = split

        with(split) {
            items.add(boardView.node)
            items.add(rightPane)
            split.dividers[0].position = 0.7
        }
        commentsView.build()

        val passB = Button("Pass")
        passB.addEventHandler(ActionEvent.ACTION) { onPass() }

        val restartB = Button("Restart")
        restartB.addEventHandler(ActionEvent.ACTION) { onRestart() }
        passB.addEventHandler(ActionEvent.ACTION) { onPass() }

        toolBar.items.addAll(passB, restartB)

        game.root.apply(game)

        return this
    }

    fun onPass() {
        game.pass(game.playerToMove)
    }

    fun onRestart() {
        game.rewindTo(game.root)
    }

    override fun moved() {
        val currentNode = game.currentNode

        for (child in currentNode.children) {
            if (child is MoveNode) {
                val mark = SquareMark(child.point)
                game.addMark(mark)
            }
        }
    }

    override fun tidyUp() {
        game.tidyUp()
        boardView.tidyUp()
    }

}
