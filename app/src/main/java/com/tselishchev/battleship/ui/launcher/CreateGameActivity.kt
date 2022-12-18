package com.tselishchev.battleship.ui.launcher

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import com.tselishchev.battleship.R
import com.tselishchev.battleship.databinding.ActivityCreateGameBinding
import com.tselishchev.battleship.ui.game.createField.CreateFieldActivity
import com.tselishchev.battleship.ui.game.GameIntentContext
import com.tselishchev.battleship.ui.profile.ProfileActivity
import com.tselishchev.battleship.utils.setSafeOnClickListener

class CreateGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateGameBinding
    private val viewModel by viewModels<CreateGameViewModel>()
    private lateinit var context: GameIntentContext


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateGameBinding.inflate(layoutInflater)
        context = GameIntentContext.getFrom(intent)

        setContentView(binding.root)

        onGameFound()

        binding.run {
            createNewGameButton.setSafeOnClickListener {
                viewModel.createGame()
            }

            joinGameButton.setSafeOnClickListener {
                viewModel.joinGame(gameEditText.text.toString())
            }

            buttonProfile.setSafeOnClickListener {
                val context = GameIntentContext(user = this@CreateGameActivity.context.user)
                val newIntent = Intent(this@CreateGameActivity, ProfileActivity::class.java)
                GameIntentContext.setTo(newIntent, context)
                startActivity(newIntent)

            }
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.profile_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
//        R.id.action_profile -> {
//            val newIntent = Intent(this@CreateGameActivity, ProfileActivity::class.java)
//            startActivity(newIntent)
//            true
//        }
//
//        else -> {
//            // If we got here, the user's action was not recognized.
//            // Invoke the superclass to handle it.
//            super.onOptionsItemSelected(item)
//        }
//    }


    private fun onGameFound() {
        viewModel.activeGame.observe(this) { game ->
            if (game == null) {
                Toast.makeText(
                    this@CreateGameActivity,
                    "Failed to get the game",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                context.game = game.id

                val newIntent = Intent(this@CreateGameActivity, CreateFieldActivity::class.java)
                GameIntentContext.setTo(newIntent, context)
                startActivity(newIntent)
            }
        }
    }
}