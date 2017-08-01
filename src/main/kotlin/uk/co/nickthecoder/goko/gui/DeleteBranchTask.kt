package uk.co.nickthecoder.goko.gui

import javafx.application.Platform
import uk.co.nickthecoder.goko.model.Game
import uk.co.nickthecoder.goko.model.GameNode
import uk.co.nickthecoder.paratask.AbstractTask
import uk.co.nickthecoder.paratask.TaskDescription

/**
 */
class DeleteBranchTask(val game: Game) : AbstractTask() {

    override val taskD = TaskDescription("Delete Branch", description = """
Delete this whole branch or the game tree,
which contains ${countNodes(game.currentNode)} nodes?
""")

    val node = game.currentNode

    override fun run() {
        if (game.currentNode === node) {
            Platform.runLater {
                game.deleteBranch()
            }
        }
    }
}

private fun countNodes(node: GameNode): Int {
    return 1 + node.children.map {
        countNodes(it)
    }.sum()
}
