package uk.co.nickthecoder.kogo.model

class TerritoryMark(point: Point, color: StoneColor) : Mark(point, color.toString().toLowerCase() + "_territory") {
}
