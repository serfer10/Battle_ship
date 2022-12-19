package com.tselishchev.battleship.ui.profile

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tselishchev.battleship.R
import com.tselishchev.battleship.db.UserRepository
import com.tselishchev.battleship.models.User
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.ByteArrayOutputStream

class ProfileViewModel : ViewModel() {


    private val userRepository = UserRepository()
    private val disposable = CompositeDisposable()
    private val _userResult = MutableLiveData<User?>()

    val userResult: LiveData<User?> = _userResult

    fun tryGetUser(id: String) {
        disposable.add(
            userRepository.getUserById(id).map { user ->
                if (user == null) {
                    return@map null
                }

                return@map user
            }.subscribeOn(Schedulers.io()).subscribe(
                {
                    _userResult.postValue(it)
                },
                {
                    _userResult.postValue(null)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun make64Base(res: Resources): String {
        //from drawable to bitmap
        val avatar = R.drawable.cat
        val bitmap = BitmapFactory.decodeResource(res, avatar)

        //from bitmap to base64
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun from64ToBitmap(encoded: String): Bitmap {
        //decoding from base64!!!!!!!!!!!!!
        val bytes: ByteArray = Base64.decode(encoded, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}