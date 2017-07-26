package uk.co.nickthecoder.kogo.gui

import javafx.scene.control.SplitPane
import uk.co.nickthecoder.kogo.ScoreEstimator
import uk.co.nickthecoder.kogo.model.*
import uk.co.nickthecoder.kogo.preferences.Preferences
import uk.co.nickthecoder.kogo.preferences.PreferencesListener
import uk.co.nickthecoder.kogo.preferences.PreferencesView
import uk.co.nickthecoder.paratask.gui.CompoundButtons
import uk.co.nickthecoder.paratask.gui.ShortcutHelper

class EditGameView(mainWindow: MainWindow, game: Game) : AbstractGoView(mainWindow, game), PreferencesListener {

    override val title = "Edit"

    private val split = SplitPane()

    private val boardView = BoardView(game)

    private val commentView = CommentsView(game, false, Preferences.editGamePreferences)

    val shortcuts = ShortcutHelper("EditGameView", node)

    override fun build() {
        super.build()
        boardView.build()
        commentView.build()
        whole.top = toolBar
        whole.center = split

        with(split) {
            items.addAll(boardView.node, commentView.node)
            dividers[0].position = 0.7
        }

        val preferencesB = KoGoActions.PREFERENCES.createButton(shortcuts) { mainWindow.addView(PreferencesView(mainWindow, Preferences.editGamePreferences)) }

        val navigation = CompoundButtons()
        navigation.children.addAll(restartB, rewindB, backB, forwardB, fastForwardB, endB)

        val mainLineB = KoGoActions.GO_MAIN_LINE.createButton(shortcuts) { history.mainLine() }

        val moveModeB = KoGoActions.MODE_MOVE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> clickToMove(point) }
        }
        moveModeB.isSelected = true

        val blackModeB = KoGoActions.MODE_BLACK.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> addSetupStone(point, StoneColor.BLACK) }
        }

        val whiteModeB = KoGoActions.MODE_WHITE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> addSetupStone(point, StoneColor.WHITE) }
        }

        val squareModeB = KoGoActions.MODE_SQUARE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> game.addMark(SquareMark(point)) }
        }

        val circleModeB = KoGoActions.MODE_CIRCLE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> game.addMark(CircleMark(point)) }
        }

        val triangleModeB = KoGoActions.MODE_TRIANGLE.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> game.addMark(TriangleMark(point)) }
        }

        val numberModeB = KoGoActions.MODE_NUMBERS.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> addNumber(point) }
        }

        val letterModeB = KoGoActions.MODE_LETTERS.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> addLetter(point) }
        }

        val removeMarkModeB = KoGoActions.MODE_CLEAR.createToggleButton(shortcuts) {
            boardView.clickBoardView.onClickedPoint = { point -> game.removeMark(point) }
        }

        val modes = CompoundButtons()
        modes.children.addAll(moveModeB, blackModeB, whiteModeB, squareModeB, circleModeB, triangleModeB, numberModeB, letterModeB, removeMarkModeB)
        modes.createToggleGroup()

        val estimateScoreB = KoGoActions.ESTIMATE_SCORE.createButton { onEstimateScore() }

        toolBar.items.addAll(saveB, preferencesB, modes, navigation, mainLineB, estimateScoreB, passB)

        labelContinuations()
        preferencesChanged()
        Preferences.listeners.add(this)
    }

    override fun tidyUp() {
        super.tidyUp()
        game.tidyUp()
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

    fun onEstimateScore() {
        ScoreEstimator(game).estimate() {
            commentView.messageLabel.text = it
        }
    }

    override fun moved() {
        super.moved()
        labelContinuations()
    }

    override fun preferencesChanged() {
        boardView.showMoveNumbers = Preferences.editGameShowMoveNumber!!
    }
}
