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

    private var history = mutableListOf<GameNode>()

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

        val restartB = Button("<<")
        restartB.addEventHandler(ActionEvent.ACTION) { onRestart() }
        passB.addEventHandler(ActionEvent.ACTION) { onPass() }

        val backB = Button("<")
        backB.addEventHandler(ActionEvent.ACTION) { onBack() }

        val forwardB = Button(">")
        forwardB.addEventHandler(ActionEvent.ACTION) { onForward() }

        toolBar.items.addAll(passB, restartB, backB, forwardB)

        game.root.apply(game)

        return this
    }

    fun onPass() {
        game.pass(game.playerToMove)
    }

    fun onRestart() {
        game.rewindTo(game.root)
    }

    fun onBack() {
        game.moveBack()
    }

    fun onForward() {
        val i = history.indexOf(game.currentNode)
        println("forward i=$i size = ${history.size}")
        if (i >= 0 && i < history.size - 1) {
            val node = history[i + 1]
            node.apply(game, null)
        } else {
            game.moveForward()
        }
    }

    override fun moved() {

        val currentNode = game.currentNode

        val c = history.indexOf(currentNode)
        if (c < 0) {
            val p = history.indexOf(currentNode.parent)
            if (p >= 0) {
                history = history.subList(0, p + 1)
                history.add(currentNode)
            } else {
                history.clear()
                var node: GameNode? = currentNode
                while (node != null) {
                    history.add(0, node)
                    node = node.parent
                }
            }
        }

        for (child in currentNode.children) {
            if (child is MoveNode) {
                if (!currentNode.hasMarkAt(child.point)) {
                    val mark = SquareMark(child.point)
                    game.addMark(mark)
                }
            }
        }
    }

    override fun tidyUp() {
        game.tidyUp()
        boardView.tidyUp()
    }

}
