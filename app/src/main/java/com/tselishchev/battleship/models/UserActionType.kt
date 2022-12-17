package com.tselishchev.battleship.models

enum class UserActionType(private val value: String) {
    Join("join"), // when user joins a game
    Left("left"), // when user lefts a game
    Act("act"); // when user makes a change

    companion object {
        fun fromValue(input: String): UserActionType? {
            return values().firstOrNull { it.value.equals(input, true) }
        }
    }
}