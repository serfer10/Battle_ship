package com.tselishchev.battleship.ui.game

import android.util.Log
import com.tselishchev.battleship.db.GameEventRepository
import com.tselishchev.battleship.db.GameRepository
import com.tselishchev.battleship.db.UserActionRepository
import com.tselishchev.battleship.models.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class GameEngine(
    private val gameId: String
) {
    private val userActionRepository = UserActionRepository()
    private val gameEventRepository = GameEventRepository()
    private val gameRepository = GameRepository()

    private var user1: String? = null
    private var user2: String? = null
    private var gameBoard1: GameBoard? = null
    private var gameBoard2: GameBoard? = null
    private var activeGame: Game? = null

    init {
        listenForUserActions()
    }

    private fun listenForUserActions() {
        val subscription = userActionRepository.listenForActions(gameId)
            .flatMapSingle { newAction ->
                when (newAction.type) {
                    UserActionType.Act -> {
                        return@flatMapSingle handleActEvent(newAction as ActAction)
                    }
                    UserActionType.Join -> {
                        return@flatMapSingle handleJoinEvent(newAction as JoinAction)
                    }
                    UserActionType.Left -> {
                        return@flatMapSingle handleLeftEvent(newAction as LeftAction)
                    }
                }
            }.takeUntil { gameEnded -> gameEnded }.subscribe({
                Log.d("GAME_ENGINE", "Handled user action")
            }, {
                Log.e("GAME_ENGINE", "Failed to handle user action $it")
            })

    }

    private fun handleActEvent(action: ActAction): Single<Boolean> {
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
            return Single.just(false /* game ended? */)
        }

        return getBattleUpdate(opponentBoard, opponentUser, action)
    }

    private fun handleJoinEvent(action: JoinAction): Single<Boolean> {
        if (!user1.isNullOrEmpty() && !user2.isNullOrEmpty()) {
            return Single.just(false /* game ended? */)
        }

        return gameRepository.getGame(gameId).flatMap { game ->
            if (user1.isNullOrEmpty() && game.user1 == action.user) {
                user1 = action.user
                gameBoard1 = GameBoard(GameCells(action.cells))
            } else if (user2.isNullOrEmpty() && game.user2 == action.user) {
                user2 = action.user
                gameBoard2 = GameBoard(GameCells(action.cells))
            }

            if (user1.isNullOrEmpty() || user2.isNullOrEmpty()) {
                return@flatMap Single.just(false /* game ended? */)
            }

            val firstUserIndex = (0..1).random()
            val firstUser = if (firstUserIndex == 0) user1!! else user2!!
            activeGame = game

            return@flatMap gameEventRepository.addEvent(
                StartGameEvent(
                    gameId, GameEventType.Start, firstUser
                )
            ).toSingleDefault(false /* game ended? */)
        }
    }

    private fun handleLeftEvent(action: LeftAction): Single<Boolean> {
        return endGame(null)
    }

    private fun getBattleUpdate(
        opponentBoard: GameBoard,
        opponentUser: String,
        action: ActAction
    ): Single<Boolean> {
        val cell = opponentBoard.hit(action.position)
        var nextUser: String = opponentUser

        if (cell == GameCell.BeatShip) {
            nextUser = action.user

            if (opponentBoard.isDefeated()) {
                return endGame(action.user)
            }
        }

        val cells1 = gameBoard1?.cells?.hideShips()?.flatten()
        val cells2 = gameBoard2?.cells?.hideShips()?.flatten()

        if (cells1 == null || cells2 == null) {
            return Single.just(false /* game ended? */)
        }

        return gameEventRepository.addEvent(
            UpdateGameEvent(
                gameId, GameEventType.Update, nextUser, cells1, cells2
            )
        ).toSingleDefault(false /* game ended? */)
    }

    private fun endGame(
        winner: String?,
    ): Single<Boolean> {
        val cells1 = gameBoard1?.cells?.items?.flatten()
        val cells2 = gameBoard2?.cells?.items?.flatten()

        val game = activeGame
        game?.winner = winner

        val gameSaving =
            if (game != null) gameRepository.addOrUpdateGame(game) else Completable.complete()

        val endEvent = EndGameEvent(
            gameId,
            GameEventType.End,
            winner,
            cells1 ?: emptyList(),
            cells2 ?: emptyList()
        )

        return gameEventRepository.addEvent(endEvent)
            .andThen(gameSaving)
            .toSingleDefault(true /* game ended? */)
    }

}