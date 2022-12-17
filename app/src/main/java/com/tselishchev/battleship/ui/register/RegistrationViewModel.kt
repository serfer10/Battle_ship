package com.tselishchev.battleship.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tselishchev.battleship.db.UserRepository
import com.tselishchev.battleship.models.User
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class RegistrationViewModel : ViewModel() {
    private val _userAddResult = MutableLiveData<Boolean>()

    private val userRepository = UserRepository()
    private val disposable = CompositeDisposable()

    val userAddResult: LiveData<Boolean> = _userAddResult

    fun addUser(name: String, password: String) {
        val id = UUID.randomUUID().toString()
        disposable.add(
            verifyIfUserNameExists(name)
                .flatMap { exists ->
                    if (exists) {
                        return@flatMap Single.just(false)
                    }

                    return@flatMap userRepository
                        .addOrUpdateUser(User(id, name, password))
                        .andThen(Single.just(true))
                }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { added ->
                        _userAddResult.postValue(added)
                    },
                    {
                        _userAddResult.postValue(false)
                    })
        )
    }

    private fun verifyIfUserNameExists(name: String): Single<Boolean> {
        return userRepository.getUsersByName(name).map { users -> users.isNotEmpty() }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}