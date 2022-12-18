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

    fun getUserLastGames(user: String, gameNumber: Int): Single<List<Game>> {
        val getGamesAsUser1 = getUserGames(user, "user1")
        val getGamesAsUser2 = getUserGames(user, "user2")

        return getGamesAsUser1.flatMap { user1Games ->
            return@flatMap getGamesAsUser2.map { user2Games -> user1Games + user2Games }
        }
    }

    private fun getUserGames(user: String, userType: String): Single<List<Game>> {
        return Single.create { emitter ->
            db.collection(GAMES)
                .whereEqualTo(userType, user)
                .get()
                .addOnSuccessListener {
                    if (!emitter.isDisposed) {
                        emitter.onSuccess(it.documents.map(::mapToGame))
                    }
                }.addOnFailureListener {
                    if (!emitter.isDisposed) {
                        emitter.onError(it)
                    }
                }
        }
    }

    private fun mapToGame(document: DocumentSnapshot): Game {
        return document.toObject(Game::class.java)!!
    }
}