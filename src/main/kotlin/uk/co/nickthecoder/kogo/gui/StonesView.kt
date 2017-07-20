package uk.co.nickthecoder.kogo.gui

import javafx.embed.swing.SwingFXUtils
import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import uk.co.nickthecoder.kogo.KoGo
import uk.co.nickthecoder.kogo.Player
import uk.co.nickthecoder.kogo.model.GameListener
import uk.co.nickthecoder.kogo.model.Point
import uk.co.nickthecoder.kogo.model.StoneColor
import uk.co.nickthecoder.kogo.util.array2d
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


/**
 * Renders stones in a visually appealing way. Note, that JavaFX 8 does NOT scale down images nicely, they end up
 * pixelated, so I've had to revert to using awt's scaling. This will lead to more headaches if I ever get around to
 * porting this application to Android (where AWT isn't available!). Grr.
 */
class StonesView(val boardView: BoardView) : Pane(), GameListener {

    val game = boardView.game

    val board = game.board

    var scaledWhiteStone: Image? = null

    var scaledBlackStone: Image? = null

    var oldScale = 0.0

    val array = array2d<ImageView?>(game.board.sizeX, game.board.sizeY) { null }

    init {
        game.gameListeners.add(this)
    }

    fun add(point: Point, image: Image?) {
        if (!board.contains(point)) {
            throw IllegalArgumentException("Not within the board")
        }
        removeAt(point)

        val imageView = ImageView(image)
        array[point.x][point.y] = imageView
        children.add(imageView)

        val scale = boardView.scale
        layoutInArea(imageView, point.x * scale, (board.sizeY - point.y - 1) * scale, scale, scale, 0.0, HPos.LEFT, VPos.TOP)
    }

    fun removeAt(point: Point) {
        val oldImageView = array[point.x][point.y]
        array[point.x][point.y] = null
        oldImageView?.let { children.remove(it) }
    }

    override fun stoneChanged(point: Point, byPlayer: Player?) {
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
        scaledWhiteStone = scaleImage("stoneW.png", boardView.scale.toInt())
        scaledBlackStone = scaleImage("stoneB.png", boardView.scale.toInt())
        children.clear()
        for (x in 0..board.sizeX - 1) {
            for (y in 0..board.sizeY - 1) {
                val color = board.getStoneAt(x, y)
                if (color != StoneColor.NONE) {
                    add(Point(x, y), if (color == StoneColor.BLACK) scaledBlackStone else scaledWhiteStone)
                }
            }
        }
    }

    fun scaleImage(name: String, size: Int): Image {
        val source = ImageIO.read(KoGo.resource(name))
        val scaled = source.getScaledInstance(size, size, java.awt.Image.SCALE_AREA_AVERAGING)
        return SwingFXUtils.toFXImage(toBufferedImage(scaled), null)
    }

    fun toBufferedImage(img: java.awt.Image): BufferedImage {
        if (img is BufferedImage) {
            return img
        }

        // Create a buffered image with transparency
        val bimage = BufferedImage(img.getWidth(null).toInt(), img.getHeight(null).toInt(), BufferedImage.TYPE_INT_ARGB)

        // Draw the image on to the buffered image
        val bGr = bimage.createGraphics()
        bGr.drawImage(img, 0, 0, null)
        bGr.dispose()

        // Return the buffered image
        return bimage
    }
}
