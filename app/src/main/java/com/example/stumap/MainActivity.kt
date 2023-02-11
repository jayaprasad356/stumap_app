package com.example.stumap

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stumap.adapter.UserAdapter
import com.example.stumap.databinding.ActivityMainBinding
import com.graymatter.stumap.models.User

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()
        adapter = UserAdapter(data())
        binding.recycler.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun data(): ArrayList<User> {
        return arrayListOf(
            User(
                "1",
                "SomeName",
                "1234567890",
                "23456890",
                "12345678"
            )
        )
    }

    private fun checkPermission() {
        val permission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            println("Permission isGranted")
        }
    }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
        when (it) {
            true -> { println("Permission has been granted by user") }
            false -> {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}