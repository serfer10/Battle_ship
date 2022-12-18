package com.tselishchev.battleship.ui.profile.gameStory

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tselishchev.battleship.databinding.ActivityGameStoryBinding
import com.tselishchev.battleship.databinding.ActivityProfileBinding
import com.tselishchev.battleship.ui.game.GameIntentContext
import com.tselishchev.battleship.ui.profile.ProfileViewModel
import com.tselishchev.battleship.ui.profile.UpdateProfileActivity
import com.tselishchev.battleship.utils.setSafeOnClickListener

class GameStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameStoryBinding
    private val viewModel by viewModels<GameStoryViewModel>()
    private lateinit var context: GameIntentContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameStoryBinding.inflate(layoutInflater)
        context = GameIntentContext.getFrom(intent)
        setContentView(binding.root)

        binding.run {
            gameStoryToolbar.setNavigationOnClickListener{finish()}
            }
        }
    }
