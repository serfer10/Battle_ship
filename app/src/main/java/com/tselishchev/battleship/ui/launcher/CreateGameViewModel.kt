package com.tselishchev.battleship.ui.launcher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tselishchev.battleship.db.GameRepository
import com.tselishchev.battleship.models.Game
import com.tselishchev.battleship.models.User
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CreateGameViewModel : ViewModel() {
    companion object {
        const val ID_LENGTH = 6
    }

    private val gameRepository = GameRepository()
    private val charPool: List<Char> = ('a'..'z') + ('0'..'9')
    private val _activeGame = MutableLiveData<Game?>()
    private val _newGameCreated = MutableLiveData<Game>()
    private val disposable = CompositeDisposable()

    val activeGame: LiveData<Game?> = _activeGame
    val newGameCreated: LiveData<Game> = _newGameCreated

    fun createGame(user: String) {
        val game = Game(getId(), user1 = user)
        disposable.add(
            gameRepository.addOrUpdateGame(game)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    _newGameCreated.postValue(game)
                    _activeGame.postValue(game)
                }, {
                    _activeGame.postValue(null)
                })
        )
    }

    fun joinGame(id: String, user: String) {
        disposable.add(
            gameRepository.getGame(id)
                .flatMap {
                    if (it.user2 != null || it.user1 == user) {
                        return@flatMap Single.just(null)
                    }

                    it.user2 = user
                    return@flatMap gameRepository.addOrUpdateGame(it).andThen(Single.just(it))
                }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        _activeGame.postValue(it)
                    },
                    {
                        _activeGame.postValue(null)
                    })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }


    private fun getId(): String {
        return List(ID_LENGTH) { charPool.random() }.joinToString("")
    }
}