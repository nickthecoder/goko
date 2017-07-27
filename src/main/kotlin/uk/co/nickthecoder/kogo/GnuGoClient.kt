package uk.co.nickthecoder.kogo

import uk.co.nickthecoder.kogo.model.Point

interface GnuGoClient {
    fun generatedMove(point: Point) {}
    fun topMoves(points: List<Pair<Point, Double>>) {}
    fun generatedPass() {}
    fun generatedResign() {}
    fun scoreEstimate(score: String) {}
    fun pointStatus(point: Point, status: String) {}
}
