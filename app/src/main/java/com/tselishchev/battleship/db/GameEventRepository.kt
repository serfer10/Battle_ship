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

class GameEventRepository {
    companion object {
        const val GAME_EVENTS = "game-events"
    }

    private val db = Firebase.firestore

    fun addEvent(event: GameEvent): Completable {
        return Completable.create { emitter ->
            db.collection(GAME_EVENTS).add(event).addOnSuccessListener {
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

    fun listenForEvents(gameId: String): Observable<GameEvent> {
        return Subject.create { emitter ->
            val gameUpdate = db.collection(GAME_EVENTS)
                .whereEqualTo("game", gameId)
                .addSnapshotListener { snapshots, error ->
                    if (snapshots == null || error != null || !emitter.isDisposed) {
                        return@addSnapshotListener
                    }


                    val actions = mapChangedGameEvents(snapshots)
                    emitter.onNext(actions)
                }

            emitter.setCancellable { gameUpdate.remove() }
        }.flatMap { Observable.fromIterable(it) }
    }

    private fun mapChangedGameEvents(snapshots: QuerySnapshot): List<GameEvent> {
        return snapshots.documentChanges.filter { it.type == DocumentChange.Type.ADDED }
            .map { mapEvent(it.document) }.mapNotNull { it }
    }

    private fun mapEvent(document: DocumentSnapshot): GameEvent? {
        val type = document["type"] as String
        val eventType = GameEventType.fromValue(type)

        if (eventType == GameEventType.Update) {
            return document.toObject(UpdateGameEvent::class.java)!!
        }

        if (eventType == GameEventType.Start) {
            return document.toObject(StartGameEvent::class.java)!!
        }

        if (eventType == GameEventType.End) {
            return document.toObject(EndGameEvent::class.java)!!

        }

        return null
    }
}