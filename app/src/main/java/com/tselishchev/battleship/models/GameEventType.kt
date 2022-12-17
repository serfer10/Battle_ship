package com.tselishchev.battleship.models

enum class GameEventType(private val value: String) {
    Start("start"), // when game starts
    Update("update"), // when user makes a move
    End("end"); // when someone wins

    companion object {
        fun fromValue(input: String): GameEventType? {
            return values().firstOrNull { it.value.equals(input, true) }
        }
    }
}