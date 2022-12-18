package com.tselishchev.battleship.db

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tselishchev.battleship.models.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.Subject

class UserActionRepository {
    companion object {
        const val USER_ACTIONS = "user-actions"
    }

    private val db = Firebase.firestore

    fun submitUserAction(action: UserAction): Completable {
        return Completable.create { emitter ->
            db.collection(USER_ACTIONS).add(action).addOnSuccessListener {
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

    fun listenForActions(gameId: String): Observable<UserAction> {
        return Subject.create { emitter ->
            val gameUpdate = db.collection(USER_ACTIONS)
                .whereEqualTo("game", gameId)
                .addSnapshotListener { snapshots, error ->
                    if (snapshots == null || error != null || emitter.isDisposed) {
                        return@addSnapshotListener
                    }


                    val actions = mapChangedUserActions(snapshots)
                    emitter.onNext(actions)
                }

            emitter.setCancellable { gameUpdate.remove() }
        }.flatMap { Observable.fromIterable(it) }
    }

    private fun mapChangedUserActions(snapshots: QuerySnapshot): List<UserAction> {
        return snapshots.documentChanges.filter { it.type == DocumentChange.Type.ADDED }
            .map { mapAction(it.document) }.mapNotNull { it }
    }

    private fun mapAction(document: DocumentSnapshot): UserAction? {
        val type = document["type"] as String
        val actionType = UserActionType.fromValue(type)

        if (actionType == UserActionType.Act) {
            return document.toObject(ActAction::class.java)!!
        }

        if (actionType == UserActionType.Join) {
            return document.toObject(JoinAction::class.java)!!
        }

        if (actionType == UserActionType.Left) {
            return document.toObject(LeftAction::class.java)!!
        }

        return null
    }
}