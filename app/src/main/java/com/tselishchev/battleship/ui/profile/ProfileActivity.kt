package com.tselishchev.battleship.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.tselishchev.battleship.R
import com.tselishchev.battleship.databinding.ActivityProfileBinding
import com.tselishchev.battleship.ui.game.GameIntentContext
import com.tselishchev.battleship.ui.profile.gameStory.GameStoryActivity
import com.tselishchev.battleship.utils.setSafeOnClickListener
import fr.tkeunebr.gravatar.Gravatar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream


class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()
    private lateinit var context: GameIntentContext
    private val imgUrl = 0
    private var _name = ""
    private lateinit var gravatarUrl:String
    private lateinit var firebaseUrl:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        context = GameIntentContext.getFrom(intent)
        setContentView(binding.root)
        viewModel.tryGetUser(context.user.toString())

        gravatarUrl = Gravatar.init().with("arseniy2604@gmail.com").size(100).build()
        loadAvatar(gravatarUrl)

        binding.run {
            profileToolbar.setNavigationOnClickListener { finish() }
            buttonEditProfile.setSafeOnClickListener {
                UpdateProfileActivity.start(this@ProfileActivity,context.user.toString(),firebaseUrl)
            }
            buttonGameStory.setSafeOnClickListener {
                val context = GameIntentContext(user = this@ProfileActivity.context.user)
                val newIntent = Intent(this@ProfileActivity, GameStoryActivity::class.java)
                GameIntentContext.setTo(newIntent, context)
                startActivity(newIntent)
            }

            toggle.setOnCheckedChangeListener { compoundButton, b ->
                    loadAvatar(if(!b){
                        gravatarUrl
                    }else{
                        Base64.decode(firebaseUrl,Base64.DEFAULT)
                    })

            }

        }

        viewModel.userResult.observe(this) {
            firebaseUrl= it?.avatar.toString()
            binding.userName.text=it?.name
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.tryGetUser(context.user.toString())
    }

    private fun loadAvatar(url: Any?) {
        Glide.with(this@ProfileActivity)
            .load(url)
            .circleCrop()
            .into(binding.avatar)
    }
}

