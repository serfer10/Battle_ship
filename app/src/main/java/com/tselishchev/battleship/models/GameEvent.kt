package com.tselishchev.battleship.models

interface GameEvent {
    var game: String
    var type: GameEventType // start, end, update
}

interface BattleGameEvent {
    var user1: UserGameInfo
    var user2: UserGameInfo
}

interface TurnGameEvent {
    var nextUser: String
}

data class UserGameInfo(
    var id: String = "",
    var cells: GameCellArray = emptyArray()
)

data class StartGameEvent  (
    override var game: String = "",
    override var type: GameEventType = GameEventType.Start,
    override var nextUser: String = "",
): GameEvent, TurnGameEvent

data class UpdateGameEvent  (
    override var game: String = "",
    override var type: GameEventType = GameEventType.Update,
    override var nextUser: String = "",
    override var user1: UserGameInfo = UserGameInfo(), // board for user 1
    override var user2: UserGameInfo = UserGameInfo() // board for user 2
): GameEvent, BattleGameEvent, TurnGameEvent

data class EndGameEvent  (
    override var game: String = "",
    override var type: GameEventType = GameEventType.End, // end
    var winner: String? = null,
    override var user1: UserGameInfo = UserGameInfo(), // board for user 1
    override var user2: UserGameInfo = UserGameInfo() // board for user 2
): GameEvent, BattleGameEvent