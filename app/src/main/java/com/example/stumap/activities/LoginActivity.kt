package com.example.stumap.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.stumap.MainActivity
import com.example.stumap.activities.RegisterActivity
import com.example.stumap.databinding.ActivityLoginBinding
import com.example.stumap.helper.ApiConfig
import com.example.stumap.helper.Constant
import com.example.stumap.helper.Session
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: Session
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        session = Session(this@LoginActivity)

        binding.btnlogin.setOnClickListener {
            when {
                binding.edMobile.text!!.isEmpty()
                        || binding.edMobile.length() != 10 -> {
                    binding.edMobile.requestFocus().also {
                        Toast.makeText(this@LoginActivity,"check PhoneNumber Once", Toast.LENGTH_LONG).show()
                    }
                }
                binding.edPassword.text!!.isEmpty() -> {
                    binding.edPassword.requestFocus().also {
                        Toast.makeText(this@LoginActivity,"check Password Once", Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    loginUser(binding.edMobile.text.toString(),
                        binding.edPassword.text.toString())
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            Intent(this@LoginActivity, RegisterActivity::class.java).also {
                startActivity(it)
            }
        }
    }
    private fun loginUser(mobile : String,password : String) {
        val params : MutableMap<String,String> = hashMapOf()

        params.apply {
            this[Constant.MOBILE] = mobile
            this["password"] = password
        }

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        session.setData(Constant.USER_ID,jsonArray.getJSONObject(0).getString("id"))
                        session.setBoolean("is_logged_in",true)
                        startActivity(
                            Intent(
                                this@LoginActivity,
                                MainActivity::class.java
                            ).also {
                                startActivity(it)
                            }
                        )
                        finish()
                    } else {
                        Toast.makeText(
                            this,
                            "" + jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    this,
                    java.lang.String.valueOf(response) + java.lang.String.valueOf(result),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, this@LoginActivity, Constant.LOGIN_URL, params, true)
    }
}