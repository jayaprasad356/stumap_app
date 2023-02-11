package com.example.stumap.activitie

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.stumap.MainActivity
import com.example.stumap.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {
            when {
                binding.edname.text.toString().isEmpty() -> {
                    binding.edname.error = "Enter Name".also {
                        Toast.makeText(this@RegisterActivity,"Enter Name",Toast.LENGTH_LONG).show()
                    }
                }
                binding.edEmail.text.toString().isEmpty() -> {
                    binding.edEmail.error = "Enter Email".also {
                        Toast.makeText(this@RegisterActivity,"Enter Email",Toast.LENGTH_LONG).show()
                    }
                }
                binding.edMobile.text.toString().isEmpty()
                        || binding.edMobile.text!!.length > 10 -> {
                    binding.edMobile.error = "Enter Proper Number".also {
                        Toast.makeText(this@RegisterActivity,"Enter Proper Mobile Number",Toast.LENGTH_LONG).show()
                    }
                }
                binding.edPassword.text.toString().isEmpty() -> {
                    binding.edEmail.error = "Enter Password".also {
                        Toast.makeText(this@RegisterActivity,"Enter Password",Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    registerUser()
                }
            }
        }
    }
    private fun registerUser() {
        Intent(this@RegisterActivity, MainActivity::class.java).also {
            startActivity(it)
        }
    }
}