package com.tselishchev.battleship.ui.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.tselishchev.battleship.R
import com.tselishchev.battleship.databinding.ActivityProfileBinding
import com.tselishchev.battleship.ui.game.GameIntentContext
import com.tselishchev.battleship.ui.profile.gameStory.GameStoryActivity
import com.tselishchev.battleship.utils.setSafeOnClickListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.ByteArrayOutputStream


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

        //from drawable to bitmap
        val avatar = R.drawable.cat
        val bitmap : Bitmap
        bitmap = BitmapFactory.decodeResource(resources,avatar)

        //from bitmap to base64
        val byteArrayOutputStream = ByteArrayOutputStream()
        //bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        val byteArray = byteArrayOutputStream.toByteArray()
        val encoded: String = Base64.encodeToString(byteArray, Base64.DEFAULT)


        binding.run {
            profileToolbar.setNavigationOnClickListener { finish() }
            buttonEditProfile.setSafeOnClickListener {
                val context = GameIntentContext(user = this@ProfileActivity.context.user)
                val newIntent = Intent(this@ProfileActivity, UpdateProfileActivity::class.java)
                GameIntentContext.setTo(newIntent, context)
                startActivity(newIntent)
            }
            buttonGameStory.setSafeOnClickListener {
                val context = GameIntentContext(user = this@ProfileActivity.context.user)
                val newIntent = Intent(this@ProfileActivity, GameStoryActivity::class.java)
                GameIntentContext.setTo(newIntent, context)
                startActivity(newIntent)
            }



                //decoding from base64!!!!!!!!!!!!!
//            val bytes: ByteArray = Base64.decode(sImage, Base64.DEFAULT)
//            // Initialize bitmap
//            // Initialize bitmap
//            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//            // set bitmap on imageView
//            // set bitmap on imageView
//            imageView.setImageBitmap(bitmap)
            Avatar.setImageBitmap(bitmap)
        }
    }
}