package com.tselishchev.battleship.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.tselishchev.battleship.databinding.ActivityProfileBinding
import com.tselishchev.battleship.models.User
import com.tselishchev.battleship.ui.activities.UpdateProfileActivity
import com.tselishchev.battleship.ui.game.GameIntentContext
import com.tselishchev.battleship.utils.setSafeOnClickListener
import io.reactivex.Single


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()
    private lateinit var context: GameIntentContext
    private val imgUrl=0
    private var _name = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        context = GameIntentContext.getFrom(intent)
        setContentView(binding.root)

        binding.run {
            profileToolbar.setNavigationOnClickListener { finish() }
            toolbarTitle.text = _name
            buttonEditProfile.setSafeOnClickListener {
                val context = GameIntentContext(user = this@ProfileActivity.context.user)
                val newIntent = Intent(this@ProfileActivity, UpdateProfileActivity::class.java)
                GameIntentContext.setTo(newIntent, context)
                startActivity(newIntent)
            }
        }
    }
}