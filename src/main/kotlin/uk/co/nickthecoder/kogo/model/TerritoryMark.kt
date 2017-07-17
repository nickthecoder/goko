package uk.co.nickthecoder.kogo.model

class TerritoryMark(point: Point, color: StoneColor) : SymbolMark(point, color.toString().toLowerCase() + "_territory", null) {
}
