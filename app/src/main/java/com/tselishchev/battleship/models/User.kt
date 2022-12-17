package com.tselishchev.battleship.models

data class User(
    var id: String = "",
    var name: String = "",
    var password: String = "",
    var avatar: String? = null
)