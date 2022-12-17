package com.tselishchev.battleship.models

data class Position(val x: Int, val y: Int) {
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
