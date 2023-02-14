package com.example.stumap

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stumap.adapter.UserAdapter
import com.example.stumap.databinding.ActivityMainBinding
import com.example.stumap.extras.LocationTrack
import com.example.stumap.helper.ApiConfig
import com.example.stumap.helper.Constant
import com.example.stumap.helper.Session
import com.google.gson.Gson
import com.graymatter.stumap.models.User
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UserAdapter
    private lateinit var locationTrack: LocationTrack
    private lateinit var session: Session
    private lateinit var calendar: Calendar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        session = Session(this@MainActivity)

        checkPermission()

        calendar = Calendar.getInstance()
        calendar.apply {
            this.set(Calendar.YEAR,Calendar.MONTH,Calendar.DAY_OF_MONTH)
        }

        adapter = UserAdapter(data())
        binding.recycler.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(this@MainActivity)
        }

        binding.LastUpdatedCv.setOnClickListener {
            when(binding.InfoLyt.visibility) {
                View.VISIBLE -> {
                    binding.InfoLyt.visibility = View.GONE
                }
                else -> {
                    binding.InfoLyt.visibility = View.VISIBLE
                }
            }
            TransitionManager.beginDelayedTransition(binding.LastUpdatedCv)
        }

        binding.edSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun afterTextChanged(p0: Editable?) {
                p0.toString().also { _str ->
                    if(_str.isNotEmpty()) {
                        binding.btnSearch.visibility = View.VISIBLE
                    }else{
                        binding.btnSearch.visibility = View.GONE
                    }
                    binding.btnSearch.setOnClickListener {
                        searchUserWithQuery(_str)
                    }
                }
                }

        })

    }

    private fun data(): ArrayList<User> {
        return arrayListOf(
            User(
                "1",
                "SomeName",
                "1234567890",
                "23456890",
                "gfhfhf",
                "hfghhhffhgh",
                "hgfhgfhf"
            )
        )
    }

    private fun checkPermission() {
        val permission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationTrack = LocationTrack(this@MainActivity)

            if(locationTrack.canGetLocation())  {
                updateLocationToApi(session.getData(Constant.USER_ID)
                    ,locationTrack.getLatitude().toString()
                    ,locationTrack.getLongitude().toString())
                Toast.makeText(this@MainActivity,locationTrack.getLatitude().toString(),Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this@MainActivity,"unable to get Location",Toast.LENGTH_LONG).show()
            }
        }
    }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
        when (it) {
            true -> {  locationTrack = LocationTrack(this@MainActivity)

                if(locationTrack.canGetLocation())  {
                    updateLocationToApi(session.getData(Constant.USER_ID)
                        ,locationTrack.getLatitude().toString()
                        ,locationTrack.getLongitude().toString())
                }else{
                    Toast.makeText(this@MainActivity,"unable to get Location",Toast.LENGTH_LONG).show()
                }}
            false -> {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLocationToApi(vararg data : String) {
        val params : MutableMap<String,String> = hashMapOf()

        session.apply {
            this.setData("latitude",data[1])
            this.setData("longitude",data[2])

        }
        params.apply {
            this[Constant.USER_ID] = data[0]
            this["latitude"] = data[1]
            this["longtitude"] = data[2]
        }

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        //TODO : ASSIGN JSONARRAY TO SESSION
                        binding.tvLastUpdated.text = calendar.time.toString()
                        jsonArray.getJSONObject(0).apply {
                            binding.tvName.text = this.getString(Constant.NAME)
                            binding.tvMobileNumber.text = this.getString(Constant.MOBILE)
                            binding.tvLat.text = session.getData("latitude")
                            binding.tvLang.text = session.getData("longitude")
                        }
                        Toast.makeText(this,"success",Toast.LENGTH_LONG).show()
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
        }, this, Constant.LOCATION_URL, params, true)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchUserWithQuery(query : String) {

        val params : MutableMap<String,String> = hashMapOf()

        params.apply {
            this["search"] = query.trim()
        }

        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    Log.e("response",response.toString())
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val gson = Gson()

                        for(i in jsonArray.getString(0).indices) {
                            val object1 = jsonArray.getJSONObject(i)

                            object1?.let {
                                val model = gson.fromJson(it.toString(),User::class.java)

                                model?.let {_data ->
                                    binding.recycler.adapter = UserAdapter(arrayListOf(_data)).also {_adapter ->
                                        _adapter.notifyDataSetChanged()
                                    }
                                    binding.recycler.layoutManager = LinearLayoutManager(this)
                                }
                            }

                        }
                        Toast.makeText(this,"user found",Toast.LENGTH_LONG).show()
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
        }, this, Constant.SEARCH_USER_URL, params, true)
    }
}