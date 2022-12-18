package com.tselishchev.battleship.ui.profile

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.tselishchev.battleship.databinding.ActivityProfileBinding
import com.tselishchev.battleship.models.User
import com.tselishchev.battleship.ui.game.GameIntentContext
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
        viewModel.tryTakeName(this@ProfileActivity.context.user)


        binding.run {
            profileToolbar.setNavigationOnClickListener { finish() }
            toolbarTitle.text = _name
        }


    }
}