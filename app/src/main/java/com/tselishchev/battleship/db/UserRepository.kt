package com.tselishchev.battleship.db

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tselishchev.battleship.models.User
import io.reactivex.Completable
import io.reactivex.Single

class UserRepository {
    companion object {
        const val USERS = "users"
    }

    private val db = Firebase.firestore

    fun addOrUpdateUser(user: User): Completable {
        return Completable.create { emitter ->
            db.collection(USERS).document(user.id).set(user).addOnSuccessListener {
                if (!emitter.isDisposed) {
                    emitter.onComplete()
                }
            }.addOnFailureListener {
                if (!emitter.isDisposed) {
                    emitter.onError(it)
                }
            }
        }
    }

    fun getUsersByName(name: String): Single<List<User>> {
        return Single.create { emitter ->
            db.collection(USERS).whereEqualTo("name", name).get().addOnSuccessListener {
                if (!emitter.isDisposed) {
                    val users = it.documents.map { document ->
                        document.toObject(User::class.java)!!
                    }
                    emitter.onSuccess(users)
                }
            }.addOnFailureListener {
                if (!emitter.isDisposed) {
                    emitter.onError(it)
                }
            }
        }
    }

    fun getUsersById(id: String?): Single<List<User>> {
        return Single.create { emitter ->
            db.collection(USERS).whereEqualTo("id", id).get().addOnSuccessListener {
                if (!emitter.isDisposed) {
                    val users = it.documents.map { document ->
                        document.toObject(User::class.java)!!
                    }
                    emitter.onSuccess(users)
                }
            }.addOnFailureListener {
                if (!emitter.isDisposed) {
                    emitter.onError(it)
                }
            }
        }
    }
}