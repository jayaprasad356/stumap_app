package com.example.stumap.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stumap.MainActivity
import com.example.stumap.R
import com.example.stumap.databinding.ActivityProfileBinding
import com.example.stumap.helper.ApiConfig
import com.example.stumap.helper.Constant
import com.example.stumap.helper.Session
import org.json.JSONException
import org.json.JSONObject


class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    lateinit var btnback: ImageButton
    private lateinit var session: Session

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        session = Session(this@ProfileActivity)

        btnback = findViewById(R.id.btnback);
        btnback.setOnClickListener {
            onBackPressed()
        }

        user_detail()

        binding.btnUpadte.setOnClickListener {
            when {
                binding.edname.text.toString().isEmpty() -> {
                    binding.edname.error = "Enter Name".also {
                        Toast.makeText(this@ProfileActivity,"Enter Name", Toast.LENGTH_LONG).show()
                    }
                }
                binding.edEmail.text.toString().isEmpty()
                        || !Patterns.EMAIL_ADDRESS.matcher(binding.edEmail.text.toString()).matches()
                -> {
                    binding.edEmail.error = "Enter Email".also {
                        Toast.makeText(this@ProfileActivity,"Enter Email", Toast.LENGTH_LONG).show()
                    }
                }
                binding.edMobile.text.toString().isEmpty()
                        || binding.edMobile.text!!.length != 10 -> {
                    binding.edMobile.error = "Enter Proper Number".also {
                        Toast.makeText(this@ProfileActivity,"Enter Proper Mobile Number", Toast.LENGTH_LONG).show()
                    }
                }
                binding.edPassword.text.toString().isEmpty() -> {
                    binding.edEmail.error = "Enter Password".also {
                        Toast.makeText(this@ProfileActivity,"Enter Password", Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    updateprofile(binding.edname.text.toString()
                        ,binding.edMobile.text.toString()
                        ,binding.edPassword.text.toString()
                        ,binding.edEmail.text.toString()
                    )
                }
            }
        }


       // Toast.makeText(this,""+session.getData(Constant.USER_ID),Toast.LENGTH_SHORT).show()




    }


    private fun updateprofile(vararg a: String) {
        val params: MutableMap<String, String> = hashMapOf()

        params.apply {
            this[Constant.NAME] = a[0]
            this[Constant.MOBILE] = a[1]
            this["password"] = a[2]
            this[Constant.EMAIL] = a[3]
            this[Constant.USER_ID] = session.getData(Constant.USER_ID)
        }

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        session.setData(Constant.NAME, binding.edname.text.toString())
                        session.setData(Constant.MOBILE, binding.edMobile.text.toString())


                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)

                        startActivity(
                            Intent(
                                this@ProfileActivity,
                                MainActivity::class.java
                            ).also {
                                startActivity(it)
                            }
                        )
                        finish()

                        Toast.makeText(
                            this,
                            "" + jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                        //TODO : ASSIGN JSONARRAY TO SESSION

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
        }, this, Constant.UPDATE_PROFILE, params, true)
    }
    private fun user_detail(vararg a: String) {
        val params: MutableMap<String, String> = hashMapOf()

        params.apply {
            this[Constant.USER_ID] = session.getData(Constant.USER_ID)
        }

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {




                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)

                        binding.edname.setText(jsonArray.getJSONObject(0).getString(Constant.NAME))
                        binding.edEmail.setText(jsonArray.getJSONObject(0).getString(Constant.EMAIL))
                        binding.edMobile.setText(jsonArray.getJSONObject(0).getString(Constant.MOBILE))
                        binding.edPassword.setText(jsonArray.getJSONObject(0).getString("password"))


                        //TODO : ASSIGN JSONARRAY TO SESSION

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
        }, this, Constant.USER_DETAIL, params, true)
    }

}