package com.tselishchev.battleship.models

class GameCells {
    companion object {
        fun emptyList(): GameCellList {
            return List(GameBoard.GAME_BOARD_SIZE) { MutableList(GameBoard.GAME_BOARD_SIZE) { GameCell.Empty } }
        }

        fun emptyArray(): GameCellArray {
            return Array(GameBoard.GAME_BOARD_SIZE) { Array(GameBoard.GAME_BOARD_SIZE) { GameCell.Empty } }
        }
    }

    private var _items: GameCellList

    val items: GameCellList
        get() = _items

    constructor() {
        _items = emptyList()
    }

    constructor(set: GameCellArray) : this() {
        _items = set.map { it.toMutableList() }
    }

    fun toViewCells(): ViewCells {
        return _items.flatten().mapIndexed(::mapGameCellToViewCell)
    }

    fun toArray(): GameCellArray {
        return _items.map { it.toTypedArray() }.toTypedArray()
    }

    private fun mapGameCellToViewCell(index: Int, gameCell: GameCell): ViewCell {
        return ViewCell(index, gameCell)
    }
}