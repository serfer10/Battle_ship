package com.tselishchev.battleship.models

data class ShipPosition(
    val start: Position,
    private val horizontal: Boolean,
    private val type: ShipType
) {
    val rows: IntRange
    val columns: IntRange

    /*
     * Includes rows and columns for the ship and available space around
     * Available space:
     * -----    ---
     * -xxx-    -x-
     * -----    ---
     */
    val rowsWithSpace: IntRange
    val columnsWithSpace: IntRange

    init {
        if (horizontal) {
            val endPoint = start.y + type.size()
            rows = start.x..start.x
            columns = start.y until endPoint

            rowsWithSpace = (start.x - 1)..(start.x + 1)
            columnsWithSpace = (start.y - 1)..endPoint
        } else {
            val endPoint = start.x + type.size()

            rows = start.x until endPoint
            columns = start.y..start.y

            rowsWithSpace = (start.x - 1)..endPoint
            columnsWithSpace = (start.y - 1)..(start.y + 1)
        }
    }

    fun isDestroyed(cells: GameCellList): Boolean {
        for (x in rows) {
            for (y in columns) {
                if (cells[x][y] != GameCell.BeatShip) {
                    return false
                }
            }
        }

        return true
    }
}