package uk.co.nickthecoder.kogo.gui

import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.control.SplitPane
import javafx.scene.control.ToolBar
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.kogo.model.*

open class EditGameView(mainWindow: MainWindow, val game: Game) : TopLevelView(mainWindow), GameListener {

    override val title = "Edit"

    val board: Board
        get() = game.board

    protected val whole = BorderPane()

    protected val toolBar = ToolBar()

    protected val split = SplitPane()

    protected val boardView = BoardView(game)

    protected val passB = Button("Pass")

    override val node = whole

    val history = History(game)


    init {
        game.gameListeners.add(this)
    }

    override fun build(): View {
        boardView.build()
        whole.top = toolBar
        whole.center = split

        split.items.add(boardView.node) // TODO Add comments etc on the right

        passB.addEventHandler(ActionEvent.ACTION) { onPass() }
        passB.addEventHandler(ActionEvent.ACTION) { onPass() }

        val restartB = Button("<<")
        restartB.addEventHandler(ActionEvent.ACTION) { game.rewindTo(game.root) }

        val backB = Button("<")
        backB.addEventHandler(ActionEvent.ACTION) { game.moveBack() }

        val forwardB = Button(">")
        forwardB.addEventHandler(ActionEvent.ACTION) { history.forward() }

        val endB = Button(">>")
        endB.addEventHandler(ActionEvent.ACTION) { onEnd() }

        val mainLineB = Button("Main Line")
        mainLineB.addEventHandler(ActionEvent.ACTION) { history.mainLine() }

        toolBar.items.addAll(passB, restartB, backB, forwardB, endB, mainLineB)

        labelContinuations()

        return this
    }

    fun onPass() {
        game.playerToMove.pass()
    }

    fun onEnd() {
        while (game.currentNode.children.isNotEmpty()) {
            game.currentNode.children[0].apply(game, null)
        }
    }

    fun labelContinuations() {
        val currentNode = game.currentNode
        var index = 0
        currentNode.children.filter { it is MoveNode }.map { it as MoveNode }.forEach { child ->
            val mark: Mark
            if (index == 0) {
                mark = MainLineMark(child.point)
            } else {
                mark = AlternateMark(child.point)
            }
            game.addMark(mark)
            index++
        }
    }

    override fun moved() {
        labelContinuations()
    }

    override fun tidyUp() {
        game.tidyUp()
        boardView.tidyUp()
    }
}
