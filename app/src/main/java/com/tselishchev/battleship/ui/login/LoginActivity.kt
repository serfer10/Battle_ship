package com.tselishchev.battleship.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.tselishchev.battleship.databinding.ActivityLoginBinding
import com.tselishchev.battleship.ui.game.GameIntentContext
import com.tselishchev.battleship.ui.launcher.CreateGameActivity
import com.tselishchev.battleship.ui.register.RegistrationActivity
import com.tselishchev.battleship.utils.setSafeOnClickListener
import io.reactivex.disposables.CompositeDisposable

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel>()
    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        binding.run {
            registerButton.setSafeOnClickListener {
                startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
            }

            loginEditText.addTextChangedListener { checkLoginEnabled() }
            passwordEditText.addTextChangedListener { checkLoginEnabled() }

            loginButton.setSafeOnClickListener {
                viewModel.tryLoginUser(
                    loginEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }

        listenToLogin()
        setContentView(binding.root)
    }

    private fun checkLoginEnabled() {sh
        binding.run {
            loginButton.isEnabled =
                !loginEditText.text.isNullOrBlank() && !passwordEditText.text.isNullOrBlank()
        }
    }

    private fun listenToLogin() {
        viewModel.userLoginResult.observe(this, Observer {
            if (it != null) {
                completeLogin(it)
            } else {
                failLogin()
            }
        })
    }

    private fun completeLogin(user: String) {
        val context = GameIntentContext(user = user)
        val newIntent = Intent(this@LoginActivity, CreateGameActivity::class.java)
        GameIntentContext.setTo(newIntent, context)
        startActivity(newIntent)
    }

    private fun failLogin() {
        Toast.makeText(
            this@LoginActivity,
            "Failed to login user",
            Toast.LENGTH_SHORT
        ).show()
    }
}