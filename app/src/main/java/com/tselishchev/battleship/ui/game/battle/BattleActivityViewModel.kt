package com.tselishchev.battleship.ui.game.battle

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tselishchev.battleship.db.GameEventRepository
import com.tselishchev.battleship.db.GameRepository
import com.tselishchev.battleship.db.UserActionRepository
import com.tselishchev.battleship.models.*
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class BattleActivityViewModel(
) : ViewModel() {
    companion object {
        const val LOG_TAG = "BATTLE"
    }

    private lateinit var user: String;
    private lateinit var gameId: String;
    private lateinit var game: Game
    private var initialized = false

    private val gameRepository = GameRepository()
    private val gameEvents = GameEventRepository()
    private val userActions = UserActionRepository()
    private val disposable = CompositeDisposable()

    private val _userCells = MutableLiveData(GameCells())
    private val _opponentCells = MutableLiveData(GameCells.emptyList())
    private val _userWins = MutableLiveData<Boolean>()
    private val _userTurn = MutableLiveData<Boolean>()

    val userCells: LiveData<GameCells>
        get() = _userCells

    val opponentCells: LiveData<GameCellList>
        get() = _opponentCells

    val userWins: LiveData<Boolean>
        get() = _userWins

    val userTurn: LiveData<Boolean>
        get() = _userTurn


    fun initialize(
        _user: String,
        _game: String,
        cells: GameCells
    ) {
        user = _user
        gameId = _game
        initialized = true

        _userCells.postValue(cells)

        listenForEvents()
        join(cells.items)
    }

    fun act(index: Int) {
        if (!initialized) return

        submitAction(ActAction(gameId, user, UserActionType.Act, Position(index)))
    }

    fun left() {
        if (!initialized) return

        submitAction(ActAction(gameId, user, UserActionType.Left))
    }

    private fun join(cells: GameCellList) {
        submitAction(JoinAction(gameId, user, UserActionType.Join, cells.flatten()))
    }

    private fun submitAction(action: UserAction) {
        disposable.add(
            userActions.submitUserAction(action).subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        Log.d(LOG_TAG, "added " + action.type)
                    },
                    {
                        Log.e(LOG_TAG, "failed to submit ${action.type}: $it")
                    })
        )
    }

    private fun listenForEvents() {
        disposable.add(
            gameEvents.listenForEvents(gameId).flatMapCompletable
            { event ->
                when (event.type) {
                    GameEventType.Update -> {
                        updateBoards(event as UpdateGameEvent)
                        emitUserTurn(event)
                    }
                    GameEventType.Start -> {
                        emitUserTurn(event as StartGameEvent)
                        return@flatMapCompletable getGame()
                    }
                    GameEventType.End -> {
                        updateBoards(event as EndGameEvent)
                        emitGameEnd(event)
                    }
                }

                return@flatMapCompletable Completable.complete()
            }.subscribeOn(Schedulers.io()).subscribe({}, {})
        )

    }

    private fun emitUserTurn(event: TurnGameEvent) {
        _userTurn.postValue(event.nextUser == user)
    }

    private fun emitGameEnd(event: EndGameEvent) {
        _userWins.postValue(event.winner == user)
    }

    private fun updateBoards(event: BattleGameEvent) {
        val userCells = _userCells.value

        if (userCells == null) {
            return
        }

        if (game.user1 == user) {
            userCells.updateCells(event.cells1)
            _userCells.postValue(userCells)
            _opponentCells.postValue(GameCells.fromFlatList(event.cells2))
        } else if (game.user2 == user) {
            userCells.updateCells(event.cells2)
            _userCells.postValue(userCells)
            _opponentCells.postValue(GameCells.fromFlatList(event.cells1))
        }

    }

    private fun getGame(): Completable {
        return gameRepository.getGame(gameId).flatMapCompletable {
            game = it
            return@flatMapCompletable Completable.complete()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}