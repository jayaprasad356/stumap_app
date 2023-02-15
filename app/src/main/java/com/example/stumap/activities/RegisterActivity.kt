package com.example.stumap.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stumap.databinding.ActivityRegisterBinding
import com.example.stumap.helper.ApiConfig
import com.example.stumap.helper.Constant
import com.example.stumap.helper.Session
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var session: Session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)

        session = Session(this@RegisterActivity)

        setContentView(binding.root)
        binding.btnRegister.setOnClickListener {
            when {
                binding.edname.text.toString().isEmpty() -> {
                    binding.edname.error = "Enter Name".also {
                        Toast.makeText(this@RegisterActivity,"Enter Name", Toast.LENGTH_LONG).show()
                    }
                }
                binding.edEmail.text.toString().isEmpty()
                        || !Patterns.EMAIL_ADDRESS.matcher(binding.edEmail.text.toString()).matches()
                -> {
                    binding.edEmail.error = "Enter Email".also {
                        Toast.makeText(this@RegisterActivity,"Enter Email", Toast.LENGTH_LONG).show()
                    }
                }
                binding.edMobile.text.toString().isEmpty()
                        || binding.edMobile.text!!.length != 10 -> {
                    binding.edMobile.error = "Enter Proper Number".also {
                        Toast.makeText(this@RegisterActivity,"Enter Proper Mobile Number", Toast.LENGTH_LONG).show()
                    }
                }
                binding.edPassword.text.toString().isEmpty() -> {
                    binding.edEmail.error = "Enter Password".also {
                        Toast.makeText(this@RegisterActivity,"Enter Password", Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    registerUser(binding.edname.text.toString()
                        ,binding.edMobile.text.toString()
                        ,binding.edPassword.text.toString()
                        ,binding.edEmail.text.toString()
                    )
                }
            }
        }
    }
    private fun registerUser( vararg a : String) {
        val params : MutableMap<String,String> = hashMapOf()

        params.apply {
            this[Constant.NAME] = a[0]
            this[Constant.MOBILE] = a[1]
            this["password"] = a[2]
            this[Constant.EMAIL] = a[3]
        }

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        session.setData(Constant.NAME, binding.edname.text.toString())
                        session.setData(Constant.MOBILE, binding.edMobile.text.toString())


                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        //TODO : ASSIGN JSONARRAY TO SESSION
                        startActivity(
                            Intent(
                                this@RegisterActivity,
                                LoginActivity::class.java
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
        }, this, Constant.SIGN_UP, params, true)
    }
}