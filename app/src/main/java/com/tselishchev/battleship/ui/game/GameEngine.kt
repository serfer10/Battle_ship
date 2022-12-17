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
    private val user1: UserGameInfo = UserGameInfo()
    private val user2: UserGameInfo = UserGameInfo()

    private fun listenForUserActions() {
        disposable.add(userActionRepository.listenForActions(gameId).subscribe({ newAction ->
            when (newAction.type) {
                UserActionType.Act -> {

                }
                UserActionType.Join -> {
                    handleJoinEvent(newAction as JoinAction)
                }
                UserActionType.Left -> {

                }
            }
        }, {}))
    }

    private fun handleActEvent(action: ActAction) {

    }

    private fun handleJoinEvent(action: JoinAction): Completable {
        if (user1.id.isNotEmpty() && user2.id.isNotEmpty()) {
            return Completable.complete()
        }

        return gameRepository.getGame(gameId).flatMapCompletable { game ->
            if (user1.id.isEmpty() && game.user1 == action.user) {
                user1.id = action.user
                user1.cells = action.cells
            } else if (user2.id.isEmpty() && game.user2 == action.user) {
                user2.id = action.user
                user2.cells = action.cells
            }

            if (user1.id.isNotEmpty() && user2.id.isNotEmpty()) {
                return@flatMapCompletable Completable.complete()
            }

            val firstUserIndex = (0..1).random()
            val firstUser = if (firstUserIndex == 0 ) user1.id else user2.id

            return@flatMapCompletable gameEventRepository.addEvent(StartGameEvent(gameId, GameEventType.Start, firstUser))
        }
    }
}