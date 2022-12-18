package com.tselishchev.battleship.models

class GameBoard(val cells: GameCells) {
    companion object {
        const val GAME_BOARD_SIZE = 10
    }

    private val cellList: GameCellList
        get() = cells.items

    fun isDefeated(): Boolean {
        return cellList.all { it.all { cell -> cell != GameCell.Ship } }
    }

    fun hit(position: Position): GameCell? {
        if (!position.checkPosition()) {
            return null
        }

        val cell = cellList[position.x][position.y]

        if (cell == GameCell.Empty) {
            cellList[position.x][position.y] = GameCell.Miss
            return GameCell.Miss
        }

        if (cell == GameCell.Ship) {
            beatShip(position)
            return GameCell.BeatShip
        }

        return null
    }

    private fun beatShip(position: Position) {
        cellList[position.x][position.y] = GameCell.BeatShip
        val ship = findShip(position)

        ship.position?.let {
            if (it.isDestroyed(cellList)) addMissCellsAround(ship)
        }
    }

    private fun findShip(position: Position): Ship {
        var startX = position.x
        var endX = position.x
        var startY = position.y
        var endY = position.y

        // check rows above
        while (startX > 0 && (cellList[startX - 1][position.y]).isShip()) {
            startX--
        }

        // check rows below
        while (endX + 1 < GAME_BOARD_SIZE && (cellList[endX + 1][position.y]).isShip()) {
            endX++
        }

        // check columns before
        while (startY > 0 && (cellList[position.x][startY - 1]).isShip()) {
            startY--
        }

        // check columns after
        while (endY + 1 < GAME_BOARD_SIZE && (cellList[position.x][endY + 1]).isShip()) {
            endY++
        }

        val xSize = endX - startX + 1
        val ySize = endY - startY + 1

        val horizontal = xSize == 1
        val size = if (horizontal) ySize else xSize
        val shipType = ShipType.convertFrom(size)

        return Ship(
            shipType,
            horizontal,
            ShipPosition(Position(startX, startY), horizontal, shipType)
        )
    }

    private fun addMissCellsAround(destroyedShip: Ship) {
        destroyedShip.position?.let {
            for (x in it.rowsWithSpace) {
                if (x !in 0 until GAME_BOARD_SIZE) {
                    continue
                }

                for (y in it.columnsWithSpace) {
                    if ((y in 0 until GAME_BOARD_SIZE) && (cellList[x][y] == GameCell.Empty)) {
                        cellList[x][y] = GameCell.Miss
                    }
                }
            }
        }
    }
}