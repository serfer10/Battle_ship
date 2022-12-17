package com.tselishchev.battleship.models

class ShipBoard(private val cells: GameCellList) {
    fun tryAddShip(ship: Ship): Boolean {
        val position = ship.position
        if (position != null && position.start.checkPosition() && canAddShip(ship)) {
            addShip(ship)
            return true
        }

        return false
    }

    private fun addShip(ship: Ship) {
        ship.position?.let {
            for (x in it.rows) {
                for (y in it.columns) {
                    cells[x][y] = GameCell.Ship
                }
            }
        }
    }

    private fun canAddShip(ship: Ship): Boolean {
        val position = ship.position ?: return false

        // check if we can insert ship
        for (x in position.rows) {
            if (x !in 0 until GameBoard.GAME_BOARD_SIZE) {
                return false
            }

            for (y in position.columns) {
                if (y !in 0 until GameBoard.GAME_BOARD_SIZE) {
                    return false
                }
            }
        }

        // check if space around is empty
        for (x in position.rowsWithSpace) {
            if (x !in 0 until GameBoard.GAME_BOARD_SIZE) {
                continue
            }

            for (y in position.columnsWithSpace) {
                if ((y in 0 until GameBoard.GAME_BOARD_SIZE) &&
                    (cells[x][y] !== GameCell.Empty)
                ) {
                    return false
                }
            }
        }

        return true
    }
}