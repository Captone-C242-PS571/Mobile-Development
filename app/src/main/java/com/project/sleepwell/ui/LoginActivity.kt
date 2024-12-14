package com.project.sleepwell.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.sleepwell.R
import com.project.sleepwell.data.model.UserModel
import com.project.sleepwell.data.repository.ResultState
import com.project.sleepwell.databinding.ActivityLoginBinding
import com.project.sleepwell.utils.NetworkHelper
import com.project.sleepwell.utils.showLoading
import com.project.sleepwell.utils.showToast
import com.project.sleepwell.viewModel.LoginModelView
import com.project.sleepwell.viewModel.ViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginModelView> {

        ViewModelFactory.getInstance(this)
    }

    private fun bypassLogin() {
        Toast.makeText(this, "BYPASS LOGIN!!!", Toast.LENGTH_LONG).show()
        viewModel.saveUser(
            UserModel(
                "testing",
                "test@gmail.com",
                "token"
            )
        )
        showToast("BYPASS")
        binding.progressIndicator.showLoading(false)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.showPassword.setOnClickListener {
            if (binding.passwordEditText.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                binding.passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.showPassword.text = "Hide"
            } else {
                binding.passwordEditText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.showPassword.text = "Show"
            }
            binding.passwordEditText.setSelection(binding.passwordEditText.text.length)
        }
        binding.signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
        binding.loginButton.setOnClickListener {
            bypassLogin()


            val email =
                binding.edLoginEmail.text.toString().trim()  // Menghapus spasi di awal dan akhir
            val password =
                binding.passwordEditText.text.toString().trim()  // Menghapus spasi password

            if (!NetworkHelper.isConnected(this)) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.no_internet)
                    .setMessage(R.string.no_internet_description)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        finish()
                    }
                    .show()
            } else {
                viewModel.login(
                    email, password
                ).observe(this) { result ->
                    if (result != null) {
                        when (result) {
                            is ResultState.Loading -> {
                                binding.progressIndicator.showLoading(false)

                            }

                            is ResultState.Success -> {
                                viewModel.saveUser(
                                    UserModel(
                                        result.data.data.name,
                                        email,
                                        result.data.token
                                    )
                                )
                                showToast(result.data.message)
                                binding.progressIndicator.showLoading(false)
                                val intent = Intent(this, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                                finish()
                            }

                            is ResultState.Error -> {
                                showToast(result.message)
                                binding.progressIndicator.showLoading(false)
                            }

                            else -> {

                            }
                        }
                    }
                }
            }
        }
    }
}