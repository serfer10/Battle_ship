package com.tselishchev.battleship.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.tselishchev.battleship.databinding.ActivityUpdateProfileBinding
import com.tselishchev.battleship.models.User
import com.tselishchev.battleship.ui.game.GameIntentContext
import com.tselishchev.battleship.ui.profile.ProfileViewModel
import com.tselishchev.battleship.utils.setSafeOnClickListener

class UpdateProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateProfileBinding
    private val viewModel by viewModels<UpdateProfileViewModel>()
    private lateinit var context: GameIntentContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        context = GameIntentContext.getFrom(intent)

        setContentView(binding.root)
        binding.run{
            updateProfileToolbar.setNavigationOnClickListener{finish()}
            buttonSaveInfo.setSafeOnClickListener {
                viewModel.updateUser(User(
                    id = this@UpdateProfileActivity.context.user.toString()
                    ,name = nicknameEditText.text.toString()
                    ,password = nicknameEditText.text.toString()
                    ,avatar = ""))
            }
        }
    }

    private fun checkRegistryAllowed() {
        binding.run {
            buttonSaveInfo.isEnabled =
                !nicknameEditText.text.isNullOrBlank() && !passwordEditText.text.isNullOrBlank()
        }
    }
}