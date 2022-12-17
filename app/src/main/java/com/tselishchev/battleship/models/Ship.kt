package com.tselishchev.battleship.models

data class Ship (val type: ShipType,
                 var horizontal: Boolean = true,
                 var position: ShipPosition? = null) {

}