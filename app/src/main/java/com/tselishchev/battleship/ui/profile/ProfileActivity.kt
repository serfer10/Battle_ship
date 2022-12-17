package com.tselishchev.battleship.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tselishchev.battleship.databinding.ActivityRegistrationBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}