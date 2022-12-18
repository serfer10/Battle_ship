package com.tselishchev.battleship.ui.activities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tselishchev.battleship.db.UserRepository
import com.tselishchev.battleship.models.User
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UpdateProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val disposable = CompositeDisposable()
    private val _userUpdateResult = MutableLiveData<String?>()

    val userUpdateResult: LiveData<String?> = _userUpdateResult

    fun updateUser(user:User) {
        disposable.add(
            userRepository.addOrUpdateUser(user).subscribeOn(Schedulers.io()).subscribe(
                {
                    _userUpdateResult.postValue("yes")
                },
                {
                    _userUpdateResult.postValue(null)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}