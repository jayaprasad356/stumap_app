package com.example.stumap.activitie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.stumap.MainActivity
import com.example.stumap.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnlogin.setOnClickListener {
            when {
                binding.edMobile.text!!.isEmpty()
                        || binding.edMobile.length() > 10 -> {
                    binding.edMobile.requestFocus().also {
                        Toast.makeText(this@LoginActivity,"check PhoneNumber Once",Toast.LENGTH_LONG).show()
                    }
                }
                binding.edPassword.text!!.isEmpty() -> {
                    binding.edPassword.requestFocus().also {
                        Toast.makeText(this@LoginActivity,"check Password Once",Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    loginUser()
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            Intent(this@LoginActivity,RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
    }
    private fun loginUser() {
        Intent(this@LoginActivity, MainActivity::class.java).also {
            startActivity(it)
        }
    }
}