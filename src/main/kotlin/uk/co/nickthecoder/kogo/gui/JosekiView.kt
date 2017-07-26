package uk.co.nickthecoder.kogo.gui

import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.kogo.preferences.Preferences
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

    private val commentsView = CommentsView(game, true, Preferences.josekiPreferences)

    override val node = whole

    val history = History(game)


    init {
        game.listeners.add(this)
    }

    override fun build() {
        println("Building joseki view")
        boardView.build()
        println("Built board view")

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

        val restartB = Button("<<")
        restartB.addEventHandler(ActionEvent.ACTION) { game.rewindTo(game.root) }
        passB.addEventHandler(ActionEvent.ACTION) { onPass() }

        val backB = Button("<")
        backB.addEventHandler(ActionEvent.ACTION) { game.moveBack() }

        val forwardB = Button(">")
        forwardB.addEventHandler(ActionEvent.ACTION) { history.forward() }

        toolBar.items.addAll(passB, restartB, backB, forwardB)

        game.root.apply(game)
    }

    fun onPass() {
        game.pass(game.playerToMove)
    }

    override fun moved() {

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

    override fun tidyUp() {
        game.tidyUp()
        boardView.tidyUp()
        commentsView.tidyUp()
    }

}
