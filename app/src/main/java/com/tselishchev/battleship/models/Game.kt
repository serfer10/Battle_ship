package com.tselishchev.battleship.models

import java.text.SimpleDateFormat
import java.util.*

data class Game(
    var id: String = "",
    var user1: String? = null,
    var user2: String? = null,
    var winner: String? = null
) {
    val createTime: String = SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(Date())
}