package com.tselishchev.battleship.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.tselishchev.battleship.databinding.ActivityRegistrationBinding
import com.tselishchev.battleship.ui.login.LoginActivity
import com.tselishchev.battleship.utils.setSafeOnClickListener

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
    private val viewModel by viewModels<RegistrationViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)

        checkRegistryAllowed()
        listenUserAdded()

        binding.run {
            nicknameEditText.addTextChangedListener {
                checkRegistryAllowed()
            }

            passwordEditText.addTextChangedListener {
                checkRegistryAllowed()
            }

            toolbar.setNavigationOnClickListener { finish() }

            registerButton.setSafeOnClickListener {
                viewModel.addUser(
                    nicknameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }

        setContentView(binding.root)
    }

    private fun checkRegistryAllowed() {
        binding.run {
            registerButton.isEnabled =
                !nicknameEditText.text.isNullOrBlank() && !passwordEditText.text.isNullOrBlank()
        }
    }

    private fun listenUserAdded() {
        viewModel.userAddResult.observe(
            this
        ) { added ->
            Toast.makeText(
                this@RegistrationActivity,
                if (added) "User has been added" else "Failed to add user",
                Toast.LENGTH_SHORT
            ).show()

            if (added) {
                startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
            }
        }
    }
}