package com.tselishchev.battleship.db

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tselishchev.battleship.models.Game
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.Subject

class GameRepository {
    companion object {
        const val GAMES = "games"
    }

    private val db = Firebase.firestore

    fun addOrUpdateGame(game: Game): Completable {
        return Completable.create { emitter ->
            db.collection(GAMES).document(game.id).set(game).addOnSuccessListener {
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

    fun getGame(id: String): Single<Game> {
        return Single.create { emitter ->
            db.collection(GAMES).document(id).get().addOnSuccessListener {
                if (!emitter.isDisposed) {
                    emitter.onSuccess(it)
                }
            }.addOnFailureListener {
                if (!emitter.isDisposed) {
                    emitter.onError(it)
                }
            }
        }.map(::mapToGame)
    }

    private fun listenForGameUpdates(id: String): Observable<Game> {
        return Subject.create { emitter ->
            val gameUpdate = db.collection(GAMES)
                .document(id)
                .addSnapshotListener { value, error ->
                    if (value == null || error != null || !emitter.isDisposed) {
                        return@addSnapshotListener
                    }

                    val game = mapToGame(value)
                    emitter.onNext(game)
                }

            emitter.setCancellable { gameUpdate.remove() }
        }
    }

    private fun mapToGame(document: DocumentSnapshot): Game {
        return document.toObject(Game::class.java)!!
    }
}