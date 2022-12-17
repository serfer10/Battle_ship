package com.tselishchev.battleship.models

class ShipStack {
    private var _current: Ship?
    private var stack: MutableList<Ship>

    val current: Ship?
        get() = _current

    constructor() {
        val shipStack =
            List(4) {
                Ship(ShipType.Boat)
            } + List(3) {
                Ship(ShipType.Destroyer)
            } + List(2) {
                Ship(ShipType.Cruiser)
            } + List(1) { Ship(ShipType.Battleship) }

        stack = shipStack.toMutableList()
        _current = stack.removeFirst()
    }

    fun pop(): Ship? {
        _current = stack.removeFirstOrNull()
        return _current
    }
}