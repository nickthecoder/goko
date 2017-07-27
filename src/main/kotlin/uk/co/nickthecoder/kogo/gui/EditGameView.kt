package uk.co.nickthecoder.kogo.gui

import javafx.scene.control.SplitPane
import javafx.scene.layout.BorderPane
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.kogo.preferences.PreferencesListener
import uk.co.nickthecoder.kogo.preferences.PreferencesView
import uk.co.nickthecoder.paratask.gui.CompoundButtons
import uk.co.nickthecoder.paratask.gui.ShortcutHelper

class EditGameView(mainWindow: MainWindow, game: Game) : AbstractGoView(mainWindow, game), PreferencesListener {

    override val title = "Edit"

    private val split = SplitPane()

    private val rightBorder = BorderPane()

    private val boardView = BoardView(game)

    private val gameInfoView = GameInfoView(game, false)

    private val commentView = CommentsView(game, false, Preferences.editGamePreferences)

    val shortcuts = ShortcutHelper("EditGameView", node)

    override fun build() {
        super.build()
        gameInfoView.build()
        boardView.build()
        commentView.build()
        whole.top = toolBar
        whole.center = split

        with(split) {
            items.addAll(boardView.node, rightBorder)
            dividers[0].position = 0.7
        }
        rightBorder.center = commentView.node
        rightBorder.top = gameInfoView.node

        val preferencesB = KoGoActions.PREFERENCES.createButton(shortcuts) { mainWindow.addView(PreferencesView(mainWindow, Preferences.editGamePreferences)) }

        val navigation = CompoundButtons()
        navigation.children.addAll(restartB, rewindB, backB, forwardB, fastForwardB, endB)

        val mainLineB = KoGoActions.GO_MAIN_LINE.createButton(shortcuts) { history.mainLine() }

        val moveModeB = KoGoActions.MODE_MOVE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> clickToMove(point) }
            boardView.playing()
        }
        moveModeB.isSelected = true

        val blackModeB = KoGoActions.MODE_BLACK.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> addSetupStone(point, StoneColor.BLACK) }
            boardView.placingStone(StoneColor.BLACK)
        }

        val whiteModeB = KoGoActions.MODE_WHITE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> addSetupStone(point, StoneColor.WHITE) }
            boardView.placingStone(StoneColor.WHITE)
        }

        val removeStoneModeB = KoGoActions.MODE_REMOVE_STONE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> removeStone(point) }
            boardView.removingStone()
        }


        val squareModeB = KoGoActions.MODE_SQUARE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> game.addMark(SquareMark(point)) }
            boardView.placingMark()
        }

        val circleModeB = KoGoActions.MODE_CIRCLE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> game.addMark(CircleMark(point)) }
            boardView.placingMark()
        }

        val triangleModeB = KoGoActions.MODE_TRIANGLE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> game.addMark(TriangleMark(point)) }
            boardView.placingMark()
        }

        val numberModeB = KoGoActions.MODE_NUMBERS.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> addNumber(point) }
            boardView.placingMark()
        }

        val letterModeB = KoGoActions.MODE_LETTERS.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> addLetter(point) }
            boardView.placingMark()
        }

        val removeMarkModeB = KoGoActions.MODE_REMOVE_MARK.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> game.removeMark(point) }
            boardView.removingMark()
        }

        val modes = CompoundButtons()
        modes.children.addAll(moveModeB, blackModeB, whiteModeB, removeStoneModeB, squareModeB, circleModeB, triangleModeB, numberModeB, letterModeB, removeMarkModeB)
        modes.createToggleGroup()

        boardView.showContinuations = Preferences.editGamePreferences.showContinuationsP.value!!

        toolBar.items.addAll(saveB, preferencesB, modes, navigation, mainLineB, estimateScoreB, passB)

        preferencesChanged()
        Preferences.listeners.add(this)
    }

    override fun tidyUp() {
        super.tidyUp()
        game.tidyUp()
        gameInfoView.tidyUp()
        boardView.tidyUp()
        commentView.tidyUp()
        Preferences.listeners.remove(this)
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
            node.apply(game)
        }
        node.addStone(board, point, color)
        game.updatedCurrentNode()
    }

    fun removeStone(point: Point) {
        if (board.getStoneAt(point).isStone()) {
            var node = game.currentNode
            if (node !is SetupNode) {
                node = SetupNode(game.playerToMove.color)
                game.addNode(node)
                node.apply(game)
            }
            node.removeStone(board, point)
            game.updatedCurrentNode()
        }
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

    override fun showScore(score: String) {
        gameInfoView.messageLabel.text = score
    }

    override fun moved() {
        super.moved()
    }

    override fun preferencesChanged() {
        boardView.showMoveNumbers = Preferences.editGameShowMoveNumber!!
        boardView.showContinuations = Preferences.editGamePreferences.showContinuationsP.value!!
    }
}
