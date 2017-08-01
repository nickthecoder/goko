package uk.co.nickthecoder.kogo.model

data class Point(var x: Int, var y: Int) {

    override fun toString(): String {
        return xLabels[x] + (y + 1).toString()
    }

    companion object {
        val xLabels = listOf("A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T")

        fun labelX(x: Int) = if (x in 0..18) xLabels[x] else (x + 1).toString()

        fun labelY(y: Int) = (y + 1).toString()

        fun fromString(str: String): Point {
            val x = xLabels.indexOf(str.substring(0, 1))
            val y = Integer.parseInt(str.substring(1)) - 1
            return Point(x, y)
        }
    }

}
