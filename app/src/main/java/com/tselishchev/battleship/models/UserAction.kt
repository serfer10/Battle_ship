package com.tselishchev.battleship.models

interface UserAction {
    var game: String
    var user: String
    var type: UserActionType
}

data class JoinAction(
    override var game: String = "",
    override var user: String = "",
    override var type: UserActionType = UserActionType.Join,
    val cells: GameCellArray = emptyArray()
) : UserAction

data class LeftAction(
    override var game: String = "",
    override var user: String = "",
    override var type: UserActionType = UserActionType.Left,
) : UserAction

data class ActAction(
    override var game: String = "",
    override var user: String = "",
    override var type: UserActionType = UserActionType.Act,
    val position: Position = Position(0, 0),
) : UserAction
