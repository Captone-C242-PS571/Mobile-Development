package com.project.sleepwell.ui

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.sleepwell.R
import com.project.sleepwell.data.repository.ResultState
import com.project.sleepwell.databinding.ActivitySignUpBinding
import com.project.sleepwell.utils.NetworkHelper
import com.project.sleepwell.utils.showLoading
import com.project.sleepwell.utils.showToast
import com.project.sleepwell.viewModel.RegisterModelView
import com.project.sleepwell.viewModel.ViewModelFactory

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val viewModel by viewModels<RegisterModelView> {
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.showPassword.setOnClickListener {
            if (binding.edRegisterPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                binding.edRegisterPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.showPassword.text = "Hide"
            } else {
                binding.edRegisterPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.showPassword.text = "Show"
            binding.edRegisterPassword.setSelection(binding.edRegisterPassword.text.length)
        }}
        binding.signUpButton.setOnClickListener {
            if (!NetworkHelper.isConnected(this)) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(R.string.no_internet)
                    .setMessage(R.string.no_internet_description)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        finish()
                    }
                    .show()
            } else {
                viewModel.register(
                    binding.edRegisterName.text.toString(),
                    binding.edRegisterEmail.text.toString(),
                    binding.edRegisterPassword.text.toString()
                ).observe(this) { result ->
                    if (result != null) {
                        when (result) {

                            is ResultState.Loading -> {
                                binding.progressIndicator.showLoading(true)
                            }
                            is ResultState.Success -> {
                                showToast(result.data.message)
                                binding.progressIndicator.showLoading(false)
                                startActivity(Intent(this, LoginActivity::class.java))
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
            binding.loginText.setOnClickListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

}}