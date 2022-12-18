package com.tselishchev.battleship.models

data class Position(var x: Int = -1, var y: Int = -1) {
    constructor(index: Int) : this(
        index / GameBoard.GAME_BOARD_SIZE,
        index % GameBoard.GAME_BOARD_SIZE
    ) {
    }

    fun checkPosition(): Boolean {
        return x in 0 until GameBoard.GAME_BOARD_SIZE &&
                y in 0 until GameBoard.GAME_BOARD_SIZE
    }
}
