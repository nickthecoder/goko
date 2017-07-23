package uk.co.nickthecoder.kogo.gui

import javafx.event.ActionEvent
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.kogo.KoGo
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.kogo.preferences.PreferencesListener
import uk.co.nickthecoder.kogo.preferences.PreferencesView
import uk.co.nickthecoder.paratask.gui.CompoundButtons

class EditGameView(mainWindow: MainWindow, val game: Game) : TopLevelView(mainWindow), GameListener, PreferencesListener {

    override val title = "Edit"

    val board: Board
        get() = game.board

    private val whole = BorderPane()

    private val toolBar = ToolBar()

    private val split = SplitPane()

    private val boardView = BoardView(game)

    private val passB = Button("Pass")


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

        val preferencesB = Button()
        preferencesB.graphic = ImageView(KoGo.imageResource("preferences-16.png"))
        preferencesB.addEventHandler(ActionEvent.ACTION) { mainWindow.addView(PreferencesView(mainWindow, Preferences.editGamePreferences)) }

        val restartB = Button()
        restartB.graphic = ImageView(KoGo.imageResource("go-first-16.png"))
        restartB.addEventHandler(ActionEvent.ACTION) { game.rewindTo(game.root) }

        val backB = Button()
        backB.graphic = ImageView(KoGo.imageResource("go-previous-16.png"))
        backB.addEventHandler(ActionEvent.ACTION) { game.moveBack() }

        val rewindB = Button()
        rewindB.graphic = ImageView(KoGo.imageResource("go-rew-16.png"))
        rewindB.addEventHandler(ActionEvent.ACTION) { game.moveBack(10) }

        val forwardB = Button()
        forwardB.graphic = ImageView(KoGo.imageResource("go-next-16.png"))
        forwardB.addEventHandler(ActionEvent.ACTION) { history.forward() }

        val fastForwardB = Button()
        fastForwardB.graphic = ImageView(KoGo.imageResource("go-ff-16.png"))
        fastForwardB.addEventHandler(ActionEvent.ACTION) { history.forward(10) }

        val endB = Button()
        endB.graphic = ImageView(KoGo.imageResource("go-last-16.png"))
        endB.addEventHandler(ActionEvent.ACTION) { onEnd() }


        val navigation = CompoundButtons()
        navigation.children.addAll(restartB, rewindB, backB, forwardB, fastForwardB, endB)

        val mainLineB = Button("Main Line")
        mainLineB.addEventHandler(ActionEvent.ACTION) { history.mainLine() }

        val moveModeB = ToggleButton()
        moveModeB.graphic = ImageView(KoGo.imageResource("stones-16.png"))
        moveModeB.addEventHandler(ActionEvent.ACTION) {
            boardView.clickBoardView.onClickedPoint = { point -> clickToMove(point) }
        }
        moveModeB.isSelected = true

        val blackModeB = ToggleButton()
        blackModeB.graphic = ImageView(KoGo.imageResource("stoneB-16.png"))
        blackModeB.addEventHandler(ActionEvent.ACTION) {
            boardView.clickBoardView.onClickedPoint = { point -> addSetupStone(point, StoneColor.BLACK) }
        }

        val whiteModeB = ToggleButton()
        whiteModeB.graphic = ImageView(KoGo.imageResource("stoneW-16.png"))
        whiteModeB.addEventHandler(ActionEvent.ACTION) {
            boardView.clickBoardView.onClickedPoint = { point -> addSetupStone(point, StoneColor.WHITE) }
        }

        val squareModeB = ToggleButton("□")
        squareModeB.addEventHandler(ActionEvent.ACTION) {
            boardView.clickBoardView.onClickedPoint = { point -> game.addMark(SquareMark(point)) }
        }

        val triangleModeB = ToggleButton("△")
        triangleModeB.addEventHandler(ActionEvent.ACTION) {
            boardView.clickBoardView.onClickedPoint = { point -> game.addMark(TriangleMark(point)) }
        }

        val circleModeB = ToggleButton("○")
        circleModeB.addEventHandler(ActionEvent.ACTION) {
            boardView.clickBoardView.onClickedPoint = { point -> game.addMark(CircleMark(point)) }
        }

        val numberModeB = ToggleButton("1")
        numberModeB.addEventHandler(ActionEvent.ACTION) {
            boardView.clickBoardView.onClickedPoint = { point -> addNumber(point) }
        }

        val remwoveMarkModeB = ToggleButton()
        remwoveMarkModeB.graphic = ImageView(KoGo.imageResource("clear.png"))
        remwoveMarkModeB.addEventHandler(ActionEvent.ACTION) {
            boardView.clickBoardView.onClickedPoint = { point -> game.removeMark(point) }
        }

        val letterModeB = ToggleButton("A")
        letterModeB.addEventHandler(ActionEvent.ACTION) {
            boardView.clickBoardView.onClickedPoint = { point -> addLetter(point) }
        }

        val modes = CompoundButtons()
        modes.children.addAll(moveModeB, blackModeB, whiteModeB, squareModeB, triangleModeB, circleModeB, numberModeB, letterModeB, remwoveMarkModeB)
        modes.createToggleGroup()

        toolBar.items.addAll(preferencesB, modes, navigation, mainLineB, passB)

        labelContinuations()
        preferencesChanged()
        Preferences.listeners.add(this)

        return this
    }

    fun clickToMove(point: Point) {
        val player = game.playerToMove

        if (player.canClickToPlay() && game.canPlayAt(point)) {
            player.makeMove(point)
        }
    }

    fun addSetupStone(point: Point, color: StoneColor) {
        var node = game.currentNode
        if (node !is SetupNode) {
            node = SetupNode(game.playerToMove.color)
            game.addNode(node)
            node.apply(game, null)
        }
        node.addStone(board, point, color)
        game.updatedCurrentNode()
    }

    fun addNumber(point: Point) {
        val node = game.currentNode
        for (i in 1..99) {
            val text = i.toString()
            if (!node.hasLabelMark(text)) {
                game.addMark(LabelMark(point, text))
                return
            }
        }
    }

    fun addLetter(point: Point) {
        val node = game.currentNode
        for (c in 'A'..'Z') {
            val text = c.toString()
            if (!node.hasLabelMark(text)) {
                game.addMark(LabelMark(point, text))
                return
            }
        }
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
        Preferences.listeners.remove(this)
    }

    override fun preferencesChanged() {
        boardView.showMoveNumbers = Preferences.editGameShowMoveNumber!!
    }
}
