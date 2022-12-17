package com.tselishchev.battleship.ui.game.createField

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tselishchev.battleship.models.*

class CreateFieldViewModel : ViewModel() {
    private val ships = ShipStack()
    private val gameCells = GameCells()
    private val shipBoard = ShipBoard(gameCells.items)

    private val _cells = MutableLiveData(gameCells)
    private val _ship = MutableLiveData(ships.current)

    val shipChange: LiveData<Ship?> = _ship
    val cells: LiveData<GameCells> = _cells

    fun onShipPlaced(index: Int) {
        val position = Position(index)
        val ship = ships.current ?: return

        ship.position = ShipPosition(position, ship.horizontal, ship.type)

        val shipAdded = shipBoard.tryAddShip(ship)

        if (shipAdded) {
            _ship.value = ships.pop()
            _cells.value = gameCells
        } else {
            ship.position = null
        }
    }

}
