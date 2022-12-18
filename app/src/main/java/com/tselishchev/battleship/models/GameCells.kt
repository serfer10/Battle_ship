package com.tselishchev.battleship.models

class GameCells {
    companion object {
        fun emptyList(): GameCellList {
            return List(GameBoard.GAME_BOARD_SIZE) { MutableList(GameBoard.GAME_BOARD_SIZE) { GameCell.Empty } }
        }

        fun emptyArray(): GameCellArray {
            return Array(GameBoard.GAME_BOARD_SIZE) { Array(GameBoard.GAME_BOARD_SIZE) { GameCell.Empty } }
        }

        fun toViewCells(items: Iterable<Iterable<GameCell>>): ViewCells {
            return items.flatten().mapIndexed(::mapGameCellToViewCell)
        }

        fun toViewCells(items: Array<Array<GameCell>>): ViewCells {
            return items.flatten().mapIndexed(::mapGameCellToViewCell)
        }

        fun fromFlatList(items: List<GameCell>): GameCellList {
            return items.chunked(GameBoard.GAME_BOARD_SIZE).map { row -> row.toMutableList() }
        }

        private fun mapGameCellToViewCell(index: Int, gameCell: GameCell): ViewCell {
            return ViewCell(index, gameCell)
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

    constructor(set: GameCellFlatList) : this() {
        _items = fromFlatList(set)
    }

    fun updateCells(flatCells: GameCellFlatList) {
        _items.forEachIndexed { row, gameCells ->
            gameCells.forEachIndexed { column, _ ->
                val newCell = flatCells[row * GameBoard.GAME_BOARD_SIZE + column]
                if (newCell != GameCell.Empty) {
                    _items[row][column] = newCell
                }
            }
        }
    }

    fun toViewCells(): ViewCells {
        return Companion.toViewCells(_items)
    }

    fun hideShips(): GameCellList {
        return _items.map {
            it.map { cell -> if (cell == GameCell.Ship) GameCell.Empty else cell }.toMutableList()
        }
    }

    fun toArray(): GameCellArray {
        return _items.map { it.toTypedArray() }.toTypedArray()
    }
}