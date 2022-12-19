package com.tselishchev.battleship.ui.profile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import com.tselishchev.battleship.databinding.ActivityUpdateProfileBinding
import com.tselishchev.battleship.models.User
import com.tselishchev.battleship.ui.game.GameIntentContext
import com.tselishchev.battleship.utils.setSafeOnClickListener

class UpdateProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateProfileBinding
    private val viewModel by viewModels<UpdateProfileViewModel>()
    private lateinit var context: GameIntentContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        context = GameIntentContext.getFrom(intent)

        checkRegistryAllowed()
        setContentView(binding.root)
        binding.run{
            updateProfileToolbar.setNavigationOnClickListener{finish()}

            nicknameEditText.addTextChangedListener{checkRegistryAllowed()}
            passwordEditText.addTextChangedListener{checkRegistryAllowed()}

            buttonSaveInfo.setSafeOnClickListener {
                viewModel.updateUser(User(
                    id = intent.getStringExtra(ID).toString()
                    ,name = nicknameEditText.text.toString()
                    ,password = passwordEditText.text.toString()
                    ,avatar = intent.getStringExtra(AVATAR).toString()))
            }
        }
        viewModel.userUpdateResult.observe(this){
            if(it == "yes"){
                Toast.makeText(this,"User information changed",Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkRegistryAllowed() {
        binding.run {
            buttonSaveInfo.isEnabled =
                !nicknameEditText.text.isNullOrBlank() && !passwordEditText.text.isNullOrBlank()
        }
    }
    companion object{
        const val AVATAR = "avatar"
        const val ID = "id"

        fun start(context: Context,id:String, avatar: String) {
            val intent = Intent(context, UpdateProfileActivity::class.java)
                .putExtra(AVATAR, avatar)
                .putExtra(ID, id)
            context.startActivity(intent)
        }
    }
}