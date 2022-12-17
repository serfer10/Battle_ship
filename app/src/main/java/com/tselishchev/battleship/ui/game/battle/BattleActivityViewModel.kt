package com.tselishchev.battleship.ui.game.battle

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tselishchev.battleship.db.GameEventRepository
import com.tselishchev.battleship.db.UserActionRepository
import com.tselishchev.battleship.models.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class BattleActivityViewModel(
) : ViewModel() {
    companion object {
        const val LOG_TAG = "BATTLE"
    }

    private lateinit var user: String;
    private lateinit var game: String;
    private var initialized = false

    private val gameEvents = GameEventRepository()
    private val userActions = UserActionRepository()
    private val disposable = CompositeDisposable()

    private val _userCells = MutableLiveData<GameCellArray>()
    private val _opponentCells = MutableLiveData(GameCells.emptyArray())
    private val _userWins = MutableLiveData<Boolean>()
    private val _userTurn = MutableLiveData(false)

    val userCells: LiveData<GameCellArray>
        get()  = _userCells

    fun initialize(
        _user: String,
        _game: String,
        cells: GameCellArray
    ) {
        user = _user
        game = _game
        initialized = true

        _userCells.postValue(cells)

        listenForEvents()
        join(cells)
    }

    fun act(index: Int) {
        if (!initialized) return

        submitAction(ActAction(game, user, UserActionType.Act, Position(index)))
    }

    fun left() {
        if (!initialized) return

        submitAction(ActAction(game, user, UserActionType.Left))
    }

    private fun join(cells: GameCellArray) {
        submitAction(JoinAction(game, user, UserActionType.Join, cells))
    }

    private fun submitAction(action: UserAction) {
        disposable.add(
            userActions.submitUserAction(action).subscribeOn(Schedulers.io())
                .subscribe(
                    { Log.d(LOG_TAG, "added " + action.type) },
                    { Log.e(LOG_TAG, "failed to submit " + action.type) })
        )
    }

    private fun listenForEvents() {
        disposable.add(
            gameEvents.listenForEvents(game).subscribeOn(Schedulers.io())
                .subscribe(
                    { event ->
                        when (event.type) {
                            GameEventType.Update -> {
                                updateBoards(event as UpdateGameEvent)
                                emitUserTurn(event)
                            }
                            GameEventType.Start -> {
                                emitUserTurn(event as StartGameEvent)
                            }
                            GameEventType.End -> {
                                updateBoards(event as EndGameEvent)
                                emitGameEnd(event)
                            }
                        }
                    }, { })
        )
    }

    private fun emitUserTurn(event: TurnGameEvent) {
        _userTurn.postValue(event.nextUser == user)
    }

    private fun emitGameEnd(event: EndGameEvent) {
        _userWins.postValue(event.winner == user)
    }

    private fun updateBoards(event: BattleGameEvent) {
        if (event.user1.id == user) {
            _userCells.postValue(event.user1.cells)
            _opponentCells.postValue(event.user2.cells)
        } else if (event.user2.id == user) {
            _userCells.postValue(event.user2.cells)
            _opponentCells.postValue(event.user1.cells)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}