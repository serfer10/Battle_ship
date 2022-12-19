package com.tselishchev.battleship.ui.profile.gameStory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tselishchev.battleship.db.GameRepository
import com.tselishchev.battleship.db.UserRepository
import com.tselishchev.battleship.models.Game
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class GameStoryViewModel : ViewModel() {
    private val gameRepository = GameRepository()
    private val disposable = CompositeDisposable()
    private val _statisticsResult = MutableLiveData<List<Game>?>()

    val statisticsResult: LiveData<List<Game>?> = _statisticsResult

    fun tryGetStatistics(id: String, count : Int) {
        disposable.add(
            gameRepository.getUserLastGames(id,count).map { games ->
                if (games.isEmpty()) {
                    return@map null
                }

                return@map games
            }.subscribeOn(Schedulers.io()).subscribe(
                {
                    _statisticsResult.postValue(it)
                },
                {
                    _statisticsResult.postValue(null)
                })
        )

    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}