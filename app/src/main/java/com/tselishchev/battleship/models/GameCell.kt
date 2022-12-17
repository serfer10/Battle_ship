package com.tselishchev.battleship.models

typealias GameCellList = List<MutableList<GameCell>>
typealias GameCellArray = Array<Array<GameCell>>

enum class GameCell(private val type: Int) {
    Empty(0),
    Ship(1),
    BeatShip(2),
    Miss(3);

    fun isShip(): Boolean {
        return type == 1 || type == 2
    }
}
