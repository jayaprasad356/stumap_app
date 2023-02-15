package com.example.stumap.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import com.example.stumap.R
import com.example.stumap.helper.Constant
import com.example.stumap.helper.Session


class ProfileActivity : AppCompatActivity() {

    lateinit var btnback:ImageButton
    private lateinit var session: Session
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        session = Session(this@ProfileActivity)

        btnback = findViewById(R.id.btnback);
        btnback.setOnClickListener{
            onBackPressed()
        }

       // Toast.makeText(this,""+session.getData(Constant.STARTLNG),Toast.LENGTH_SHORT).show()




    }
}