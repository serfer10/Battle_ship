package com.tselishchev.battleship.models

enum class ShipType (private val value: Int) {
    Boat(1),
    Destroyer(2),
    Cruiser(3),
    Battleship(4);

    fun size(): Int {
        return this.value
    }

    companion object {
        fun convertFrom(value: Int) = ShipType.values().first { it.value == value }
    }
}
