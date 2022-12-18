package com.tselishchev.battleship.ui.game

import com.tselishchev.battleship.db.GameEventRepository
import com.tselishchev.battleship.db.GameRepository
import com.tselishchev.battleship.db.UserActionRepository
import com.tselishchev.battleship.models.*
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable

class GameEngine(
    private val gameId: String
) {
    private val userActionRepository = UserActionRepository()
    private val gameEventRepository = GameEventRepository()
    private val gameRepository = GameRepository()
    private val disposable = CompositeDisposable()

    private var user1: String? = null
    private var user2: String? = null
    private var gameBoard1: GameBoard? = null
    private var gameBoard2: GameBoard? = null
    private var activeGame: Game? = null
    private var isGameEnded: Boolean = false

    init {
        listenForUserActions()
    }

    private fun listenForUserActions() {
        disposable.add(userActionRepository.listenForActions(gameId)
            .flatMapCompletable { newAction ->
                when (newAction.type) {
                    UserActionType.Act -> {
                        return@flatMapCompletable handleActEvent(newAction as ActAction)
                    }
                    UserActionType.Join -> {
                        return@flatMapCompletable handleJoinEvent(newAction as JoinAction)
                    }
                    UserActionType.Left -> {
                        return@flatMapCompletable handleLeftEvent(newAction as LeftAction)
                    }
                }
            }.subscribe({
                if (isGameEnded) {
                    disposable.dispose()
                }
            }, {})
        )
    }

    private fun handleActEvent(action: ActAction): Completable {
        var user: String? = null
        var userBoard: GameBoard? = null
        var opponentBoard: GameBoard? = null
        var opponentUser: String? = null

        if (action.user == user1) {
            userBoard = gameBoard1
            user = user1
            opponentBoard = gameBoard2
            opponentUser = user2
        } else if (action.user == user2) {
            userBoard = gameBoard2
            user = user2
            opponentBoard = gameBoard1
            opponentUser = user1
        }

        if (opponentBoard == null || opponentUser == null || userBoard == null || user == null) {
            return Completable.complete()
        }

        return gameEventRepository.addEvent(
            getBattleUpdate(
                userBoard, opponentBoard, user, opponentUser, action
            )
        )
    }

    private fun handleJoinEvent(action: JoinAction): Completable {
        if (!user1.isNullOrEmpty() && !user2.isNullOrEmpty()) {
            return Completable.complete()
        }

        return gameRepository.getGame(gameId).flatMapCompletable { game ->
            if (user1.isNullOrEmpty() && game.user1 == action.user) {
                user1 = action.user
                gameBoard1 = GameBoard(GameCells(action.cells))
            } else if (user2.isNullOrEmpty() && game.user2 == action.user) {
                user2 = action.user
                gameBoard2 = GameBoard(GameCells(action.cells))
            }

            if (user1.isNullOrEmpty() || user2.isNullOrEmpty()) {
                return@flatMapCompletable Completable.complete()
            }

            val firstUserIndex = 0 // (0..1).random()
            val firstUser = if (firstUserIndex == 0) user1!! else user2!!
            activeGame = game

            return@flatMapCompletable gameEventRepository.addEvent(
                StartGameEvent(
                    gameId, GameEventType.Start, firstUser
                )
            )
        }
    }

    private fun handleLeftEvent(action: LeftAction): Completable {
        val cells1 = gameBoard1?.cells?.items?.flatten()
        val cells2 = gameBoard2?.cells?.items?.flatten()

        return gameEventRepository.addEvent(
            endGame(
                null,
                cells1 ?: emptyList(),
                cells2 ?: emptyList()
            )
        )
    }

    private fun getBattleUpdate(
        userBoard: GameBoard,
        opponentBoard: GameBoard,
        user: String,
        opponentUser: String,
        action: ActAction
    ): GameEvent {
        val cell = opponentBoard.hit(action.position)
        var nextUser: String = opponentUser

        if (cell == GameCell.BeatShip) {
            nextUser = action.user

            if (opponentBoard.isDefeated()) {
                val userCells = userBoard.cells.items.flatten()
                val opponentCells = opponentBoard.cells.items.flatten()

                if (user1 == user) {
                    return endGame(action.user, userCells, opponentCells)
                }

                return endGame(action.user, opponentCells, userCells)
            }
        }

        val userCells = userBoard.cells.hideShips().flatten()
        val opponentCells = opponentBoard.cells.hideShips().flatten()
        if (user == user1) {
            return UpdateGameEvent(
                gameId, GameEventType.Update, nextUser, userCells, opponentCells
            )
        }

        return UpdateGameEvent(
            gameId, GameEventType.Update, nextUser, opponentCells, userCells
        )
    }

    private fun endGame(
        winner: String?,
        cells1: GameCellFlatList,
        cells2: GameCellFlatList
    ): GameEvent {
        isGameEnded = true
        return EndGameEvent(gameId, GameEventType.End, winner, cells1, cells2)
    }

}