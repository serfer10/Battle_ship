package com.tselishchev.battleship.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tselishchev.battleship.db.UserRepository
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LoginViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val disposable = CompositeDisposable()
    private val _userLoginResult = MutableLiveData<String?>()

    val userLoginResult: LiveData<String?> = _userLoginResult

    fun tryLoginUser(name: String, password: String) {
        disposable.add(
            userRepository.getUsersByName(name).map { users ->
                if (users.isEmpty()) {
                    return@map null
                }

                val user = users[0]
                return@map if (user.password == password) user.id else null
            }.subscribeOn(Schedulers.io()).subscribe(
                {
                    _userLoginResult.postValue(it)
                },
                {
                    _userLoginResult.postValue(null)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}