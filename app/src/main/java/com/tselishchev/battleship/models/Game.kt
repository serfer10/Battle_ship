package com.tselishchev.battleship.models

data class Game(
    var id: String = "",
    var user1: String? = null,
    var user2: String? = null,
    var winner: String? = null
)