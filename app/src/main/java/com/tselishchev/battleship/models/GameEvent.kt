package com.tselishchev.battleship.models

interface GameEvent {
    var game: String
    var type: GameEventType // start, end, update
}

interface BattleGameEvent {
    var cells1: List<GameCell>
    var cells2: List<GameCell>
}

interface TurnGameEvent {
    var nextUser: String
}

data class StartGameEvent  (
    override var game: String = "",
    override var type: GameEventType = GameEventType.Start,
    override var nextUser: String = "",
): GameEvent, TurnGameEvent

data class UpdateGameEvent  (
    override var game: String = "",
    override var type: GameEventType = GameEventType.Update,
    override var nextUser: String = "",
    override var cells1: List<GameCell> = emptyList(), // board for user 1
    override var cells2: List<GameCell> = emptyList() // board for user 2
): GameEvent, BattleGameEvent, TurnGameEvent

data class EndGameEvent  (
    override var game: String = "",
    override var type: GameEventType = GameEventType.End, // end
    var winner: String? = null,
    override var cells1: List<GameCell> = emptyList(), // board for user 1
    override var cells2: List<GameCell> = emptyList() // board for user 2
): GameEvent, BattleGameEvent