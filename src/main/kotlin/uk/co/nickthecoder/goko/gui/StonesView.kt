/*
GoKo a Go Client
Copyright (C) 2017 Nick Robinson

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
package uk.co.nickthecoder.goko.gui

import javafx.embed.swing.SwingFXUtils
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import uk.co.nickthecoder.goko.GoKo
import uk.co.nickthecoder.goko.model.GameListener
import uk.co.nickthecoder.goko.model.Point
import uk.co.nickthecoder.goko.model.StoneColor
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


/**
 * Renders stones in a visually appealing way. Note, that JavaFX 8 does NOT scale down images nicely, they end up
 * pixelated, so I've had to revert to using AWT's scaling. This will lead to more headaches if I ever get around to
 * porting this application to Android (where AWT isn't available!). Grr.
 */
class StonesView(val boardView: BoardView) : Pane(), GameListener {

    val game = boardView.game

    val board = game.board

    var scaledWhiteStone: Image? = null

    var scaledBlackStone: Image? = null

    var oldScale = 0.0

    val array = List(board.size, { MutableList<ImageView?>(board.size, { null }) })

    init {
        game.listeners.add(this)
    }

    private fun add(point: Point, image: Image?) {
        if (!board.contains(point)) {
            throw IllegalArgumentException("Not within the board")
        }
        removeAt(point)

        val imageView = ImageView(image)
        array[point.x][point.y] = imageView

        children.add(imageView)

        val scale = boardView.scale
        layoutInArea(imageView, point.x * scale, (board.size - point.y - 1) * scale, scale, scale, 0.0, HPos.LEFT, VPos.TOP)
    }

    private fun removeAt(point: Point) {
        val oldImageView = array[point.x][point.y]
        array[point.x][point.y] = null
        oldImageView?.let { children.remove(it) }
    }

    override fun stoneChanged(point: Point) {
        val color = board.getStoneAt(point)
        if (color == StoneColor.NONE) {
            removeAt(point)
        } else {
            add(point, if (color == StoneColor.BLACK) scaledBlackStone else scaledWhiteStone)
        }
    }

    override fun layoutChildren() {
        if (oldScale == boardView.scale) {
            return
        }
        resetStones()
    }

    private fun resetStones() {
        scaledWhiteStone = scaleImage("stoneW.png", boardView.scale.toInt())
        scaledBlackStone = scaleImage("stoneB.png", boardView.scale.toInt())
        children.clear()
        for (x in 0..board.size - 1) {
            for (y in 0..board.size - 1) {
                val color = board.getStoneAt(x, y)
                if (color.isStone()) {
                    val point = Point(x, y)
                    val displayColor = game.variation.displayColor(point)

                    when (displayColor) {
                        StoneColor.WHITE -> add(point, scaledWhiteStone)
                        StoneColor.BLACK -> add(point, scaledBlackStone)
                        else -> {
                            // Do nothing
                        }
                    }
                }
            }
        }
    }

    fun scaleImage(name: String, size: Int): Image {
        val source = ImageIO.read(GoKo.resource(name))
        val scaled = source.getScaledInstance(size, size, java.awt.Image.SCALE_AREA_AVERAGING)
        return SwingFXUtils.toFXImage(toBufferedImage(scaled), null)
    }

    fun toBufferedImage(img: java.awt.Image): BufferedImage {
        if (img is BufferedImage) {
            return img
        }

        // Create a buffered image with transparency
        val bufferedImage = BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)

        // Draw the image on to the buffered image
        val bGr = bufferedImage.createGraphics()
        bGr.drawImage(img, 0, 0, null)
        bGr.dispose()

        // Return the buffered image
        return bufferedImage
    }
}
