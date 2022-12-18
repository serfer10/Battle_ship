package com.tselishchev.battleship.ui.game

import android.content.Intent
import android.os.Bundle
import com.tselishchev.battleship.models.GameCellArray

data class GameIntentContext(
    var user: String? = null,
    var game: String? = null,
    var cells: GameCellArray? = null
) {
    fun isFulfilled(): Boolean {
        return cells != null && game != null && user != null
    }

    fun resetGameInfo() {
        cells = null
        game = null
    }

    companion object {
        const val GAME_CELLS = "GAME_CELLS"
        const val USER_ID = "USER_ID"
        const val GAME_ID = "GAME_ID"

        fun getFrom(intent: Intent): GameIntentContext {
            val cells = intent.extras?.getSerializable(GAME_CELLS) as GameCellArray?
            val user = intent.extras?.getString(USER_ID)
            val game = intent.extras?.getString(GAME_ID)

            return GameIntentContext(user, game, cells)
        }

        fun setTo(intent: Intent, context: GameIntentContext) {
            val contextBundle = Bundle()

            contextBundle.putSerializable(GAME_CELLS, context.cells)
            contextBundle.putSerializable(USER_ID, context.user)
            contextBundle.putSerializable(GAME_ID, context.game)

            intent.putExtras(contextBundle)
        }
    }
}