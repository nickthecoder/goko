package uk.co.nickthecoder.goko.model

class Board(val size: Int, val game: Game) {

    private val points = List(size, { MutableList(size, { StoneColor.NONE }) })

    var isCopy = false

    fun contains(x: Int, y: Int) = x >= 0 && y >= 0 && x < size && y < size

    fun contains(point: Point) = point.x >= 0 && point.y >= 0 && point.x < size && point.y < size

    fun getStoneAt(x: Int, y: Int): StoneColor {
        if (contains(x, y)) {
            return points[x][y]
        } else {
            return StoneColor.EDGE
        }
    }

    fun getStoneAt(point: Point): StoneColor {
        if (contains(point)) {
            return points[point.x][point.y]
        } else {
            return StoneColor.EDGE
        }
    }

    fun setStoneAt(point: Point, color: StoneColor) {
        if (contains(point)) {
            points[point.x][point.y] = color
            if (!isCopy) {
                game.listeners.forEach { listener ->
                    listener.stoneChanged(point)
                }
            }
        }
    }

    fun removeStoneAt(point: Point) {
        points[point.x][point.y] = StoneColor.NONE
        if (!isCopy) {
            game.listeners.forEach { listener ->
                listener.stoneChanged(point)
            }
        }
    }

    fun checkLiberties(point: Point): Set<Point>? {

        val color = getStoneAt(point)
        val opposite = if (color == StoneColor.BLACK) StoneColor.WHITE else StoneColor.BLACK
        val group = mutableSetOf<Point>()

        fun surrounded(innerPoint: Point): Boolean {
            val c = getStoneAt(innerPoint)
            if (c == StoneColor.NONE) {
                return false
            }
            if (c == StoneColor.EDGE || c == opposite) {
                return true
            }
            if (group.contains(innerPoint)) {
                return true
            }
            group.add(innerPoint)

            return surrounded(Point(innerPoint.x - 1, innerPoint.y)) &&
                    surrounded(Point(innerPoint.x + 1, innerPoint.y)) &&
                    surrounded(Point(innerPoint.x, innerPoint.y - 1)) &&
                    surrounded(Point(innerPoint.x, innerPoint.y + 1))
        }

        if (surrounded(point)) {
            return group
        } else {
            return null
        }
    }

    fun removeTakenStones(point: Point): Set<Point> {

        fun removeTakenStonesQuarter(innerPoint: Point): Set<Point> {
            if (contains(innerPoint) && getStoneAt(innerPoint) != getStoneAt(point)) {
                val group = checkLiberties(innerPoint)
                group?.forEach {
                    removeStoneAt(it)
                }
                return group ?: setOf<Point>()
            } else {
                return setOf()
            }
        }

        val removedPoints = mutableSetOf<Point>()
        removedPoints += removeTakenStonesQuarter(Point(point.x - 1, point.y))
        removedPoints += removeTakenStonesQuarter(Point(point.x + 1, point.y))
        removedPoints += removeTakenStonesQuarter(Point(point.x, point.y - 1))
        removedPoints += removeTakenStonesQuarter(Point(point.x, point.y + 1))

        return removedPoints
    }

    fun copy(): Board {
        val result = Board(size, game)
        result.isCopy = true
        for (x in 0..size - 1) {
            for (y in 0..size - 1) {
                result.points[x][y] = points[x][y]
            }
        }
        return result
    }

    /**
     * The hash is used to detect kos. If the stones on the board are the same as a previous position, and
     * the player to move is the same, then we have a repeated board position, which is illegal Ko move.
     */
    override fun hashCode(): Int {
        return points.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is Board) {
            return other.points == this.points
        } else {
            return false
        }
    }
}
