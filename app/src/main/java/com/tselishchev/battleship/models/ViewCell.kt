package com.tselishchev.battleship.models

typealias ViewCells = List<ViewCell>

data class ViewCell(
    val position: Int,
    val type: GameCell
): java.io.Serializable
