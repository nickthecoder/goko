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

import javafx.scene.input.KeyCode

object GoKoActions {

    private val nameToActionMap = mutableMapOf<String, GoKoAction>()

    // General
    val ESCAPE = GoKoAction("general.escape", KeyCode.ESCAPE)

    // MainWindow
    val CLOSE_TAB = GoKoAction("tab.close", KeyCode.W, control = true, tooltip = "Close Tab")
    val OPEN_GAME_FILE = GoKoAction("file.open", KeyCode.O, control = true, tooltip = "Open Game File")
    val PREFERENCES = GoKoAction("preferences", KeyCode.P, control = true, tooltip = "Preferences")
    val JOSEKI_DICTIONARY = GoKoAction("joseki.database", KeyCode.J, control = true, tooltip = "Open Joseki Database")

    // ProblemSetView
    val PROBLEMS_RESET = GoKoAction("reset", null, null, label = "Reset", tooltip = "Reset Problem Results")

    // Various views
    val PASS = GoKoAction("pass", KeyCode.P, alt = true, label = "Pass")
    val REVIEW = GoKoAction("review", KeyCode.R, control = true, label = "Review", tooltip = "Review Game")
    val SAVE = GoKoAction("save", KeyCode.S, control = true, label = "Save", tooltip = "Save Game")
    val RESIGN = GoKoAction("resign", null, label = "Resign", tooltip = "Resign Game")
    val HINT = GoKoAction("hint", KeyCode.H, control = true, label = "Hint", tooltip = "Show where GnuGo would move")
    val ESTIMATE_SCORE = GoKoAction("estimateScore", KeyCode.E, control = true, label = "Score", tooltip = "Estimate Score")
    val HOTSPOTS = GoKoAction("hotspots", KeyCode.T, control = true, label = "Hotspots", tooltip = "Territory changes depending on whose turn it is")
    val UNDO = GoKoAction("undo", KeyCode.Z, control = true, tooltip = "Undo")
    val CHECK_GNU_GO = GoKoAction("checkGnuGo", KeyCode.G, control = true, shift = true, tooltip = "Check GnuGo's board is the same as GoKo's")

    // EditGameView
    val GO_FIRST = GoKoAction("go-first", KeyCode.HOME, alt = null, tooltip = "Rewind to the beginning")
    val GO_REWIND = GoKoAction("go-rewind", KeyCode.PAGE_UP, alt = null, tooltip = "Go back 10 moves")
    val GO_BACK = GoKoAction("go-back", KeyCode.LEFT, alt = null, tooltip = "Go back 1 move")
    val GO_FORWARD = GoKoAction("go-forward", KeyCode.RIGHT, alt = null, tooltip = "Go forward 1 move")
    val GO_FAST_FORWARD = GoKoAction("go-fastForward", KeyCode.PAGE_DOWN, alt = null, tooltip = "Fast Forward 10 moves")
    val GO_END = GoKoAction("go-last", KeyCode.END, alt = null, tooltip = "Fast forward to the end")

    val MODE_MOVE = GoKoAction("mode-move", KeyCode.F1, control = null, tooltip = "Mode : Make moves")
    val MODE_BLACK = GoKoAction("mode-black", KeyCode.F2, control = null, tooltip = "Mode : Place black set-up stones")
    val MODE_WHITE = GoKoAction("mode-white", KeyCode.F3, control = null, tooltip = "Mode : Place white set-up stones")
    val MODE_REMOVE_STONE = GoKoAction("mode-remove-stone", KeyCode.F4, control = null, tooltip = "Mode : Remove stones")
    val MODE_SQUARE = GoKoAction("mode-square", KeyCode.F5, label = "□", control = null, tooltip = "Mode : Add square marks")
    val MODE_CIRCLE = GoKoAction("mode-square", KeyCode.F6, label = "○", control = null, tooltip = "Mode : Add circle marks")
    val MODE_TRIANGLE = GoKoAction("mode-triangle", KeyCode.F7, label = "△", control = null, tooltip = "Mode : Add triangle marks")
    val MODE_NUMBERS = GoKoAction("mode-numbers", KeyCode.F8, label = "1", control = null, tooltip = "Mode : Add number marks")
    val MODE_LETTERS = GoKoAction("mode-letters", KeyCode.F9, label = "A", control = null, tooltip = "Mode : Add letter marks")
    val MODE_REMOVE_MARK = GoKoAction("mode-remove-mark", KeyCode.F10, control = null, tooltip = "Mode : Remove marks")
    val MODE_DIM = GoKoAction("mode-dim", KeyCode.F11, control = null, tooltip = "Mode : Make stones transparent")

    val EDIT_GAME_INFO = GoKoAction("game-info", keyCode = null, label = "Game Info")
    val DELETE_BRANCH = GoKoAction("delete-branch", keyCode = null, label = "Delete Branch")
    val GNU_GO_TO_PLAY = GoKoAction("gnu-go-to-play", keyCode = null, label = "GnuGo to Play")
    val REWIND_TO_BRANCH_POINT = GoKoAction("rewind-to-branch-point", keyCode = KeyCode.UP, control = null, tooltip = "Go back to a branch point")
    val FORWARD_TO_BRANCH_POINT = GoKoAction("forward-to-branch-point", keyCode = KeyCode.DOWN, control = null, tooltip = "Go forward to a branch point")
    val REWIND_TO_MAIN_LINE = GoKoAction("rewind-to-main-line", KeyCode.UP, alt = true, tooltip = "Rewind to the main line of play")

    // ProblemView

    val PROBLEM_RESTART = GoKoAction("problem-reload", KeyCode.F5, control = true, tooltip = "Restart Problem")
    val PROBLEM_GIVE_UP = GoKoAction("problem-give-up", null, label = "Give Up", tooltip = "Give Up and show the solution")
    val PROBLEM_NEXT = GoKoAction("problem-next", KeyCode.RIGHT, alt = true, tooltip = "Next problem")

    fun add(action: GoKoAction) {
        GoKoActions.nameToActionMap.put(action.name, action)
    }
}
