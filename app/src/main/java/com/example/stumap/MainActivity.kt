package com.example.stumap

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stumap.activities.LoginActivity
import com.example.stumap.activities.ProfileActivity
import com.example.stumap.adapter.UserAdapter
import com.example.stumap.databinding.ActivityMainBinding
import com.example.stumap.extras.LocationTrack
import com.example.stumap.helper.ApiConfig
import com.example.stumap.helper.Constant
import com.example.stumap.helper.Constant.SUCCESS
import com.example.stumap.helper.Session
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.graymatter.stumap.models.User
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: UserAdapter
    private lateinit var locationTrack: LocationTrack
    private lateinit var session: Session
    private lateinit var calendar: Calendar
    var activity: Activity? = null
    var fm: FragmentManager? = null
    private var navbar: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navbar = binding.bottomNavigation
        activity = this@MainActivity

        fm = supportFragmentManager
        fm!!.beginTransaction().replace(R.id.Container, HomeFragment())
            .commitAllowingStateLoss()
        session = Session(this@MainActivity)

        checkPermission()
        gpslocation()

        navbar!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {

                R.id.Home ->
                    fm!!.beginTransaction().replace(R.id.Container, HomeFragment())
                        .commitAllowingStateLoss()

                R.id.FindFriends ->
                    fm!!.beginTransaction().replace(R.id.Container, FindFriendsFragment())
                        .commitAllowingStateLoss()

                R.id.RequestFriends -> fm!!.beginTransaction()
                    .replace(R.id.Container, RequestFragment())
                    .commitAllowingStateLoss()

            }
            true
        })

        binding.fab.setOnClickListener(
            //logout session
            View.OnClickListener {
                session.logoutUser(activity)
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
        )

        calendar = Calendar.getInstance()
        calendar.apply {
            this.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH)
        }


        val latitude = locationTrack.getLatitude().toString()
        val longitude = locationTrack.getLongitude().toString()

        session.setData(Constant.STARTLAT, latitude)
        session.setData(Constant.STARTLNG, longitude)
//
//        Toast.makeText(activity,""+latitude,Toast.LENGTH_SHORT).show()
//        Toast.makeText(activity,""+longitude,Toast.LENGTH_SHORT).show()


        var search = ""

        try {
           // data(search)
        } catch (e: Exception) {
            // Handle the exception here
            // For example, you could log the error message or show an error dialog to the user
        }


//        binding!!.Name.text = session.getData(Constant.NAME)
//        binding!!.mobile.text = session.getData(Constant.MOBILE)


        binding!!.ivprofile.setOnClickListener {

            val intent = Intent(activity, ProfileActivity::class.java)
            startActivity(intent)

        }


        binding!!.recycler.setLayoutManager(
            LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
        )




        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                if (binding.edSearch.text.length == 10) {

                    //   Toast.makeText(activity,""+binding.edSearch.text.toString(),Toast.LENGTH_SHORT).show()

                    var search = binding.edSearch.text.toString()
                    data(search)
                } else {
                    var search = ""
                    data(search)

                }
            }

            override fun afterTextChanged(s: Editable?) {


            }

        })








        binding.LastUpdatedCv.setOnClickListener {
            when (binding.InfoLyt.visibility) {
                View.VISIBLE -> {
                    binding.InfoLyt.visibility = View.GONE
                }
                else -> {
                    binding.InfoLyt.visibility = View.VISIBLE
                }
            }
            TransitionManager.beginDelayedTransition(binding.LastUpdatedCv)
        }


    }

    @SuppressLint("SuspiciousIndentation")
    private fun data(search: String) {
        val params = HashMap<String, String>()
        params[Constant.SEARCH] = search
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(SUCCESS)) {
                        Log.d("user", response)
                        val jsonArray: JSONArray = jsonObject.getJSONArray(Constant.DATA)
                        val g = Gson()
                        val userList: ArrayList<User> = ArrayList<User>()
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            if (jsonObject1 != null) {
                                val group: User =
                                    g.fromJson(jsonObject1.toString(), User::class.java)
                                userList.add(group)
                            } else {
                                break
                            }
                        }
                        val adapter = activity?.let { UserAdapter(it, userList, "view") }
                        binding!!.recycler.setAdapter(adapter)
                    } else {
                        Toast.makeText(
                            activity,
                            jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.USER_LIST_URL, params, true)


    }


    private fun checkPermission() {
        val permission = ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationTrack = LocationTrack(this@MainActivity)

            if (locationTrack.canGetLocation()) {
                updateLocationToApi(
                    session.getData(Constant.USER_ID),
                    locationTrack.getLatitude().toString(),
                    locationTrack.getLongitude().toString()
                )


                //  Toast.makeText(this@MainActivity,locationTrack.getLatitude().toString(),Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@MainActivity, "unable to get Location", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        when (it) {
            true -> {
                locationTrack = LocationTrack(this@MainActivity)

                if (locationTrack.canGetLocation()) {
                    updateLocationToApi(
                        session.getData(Constant.USER_ID),
                        locationTrack.getLatitude().toString(),
                        locationTrack.getLongitude().toString()
                    )


                } else {
                    Toast.makeText(this@MainActivity, "unable to get Location", Toast.LENGTH_LONG)
                        .show()
                }
            }
            false -> {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLocationToApi(vararg data: String) {
        val params: MutableMap<String, String> = hashMapOf()

        session.apply {
            this.setData("latitude", data[1])
            this.setData("longitude", data[2])

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
                            binding.tvLat.text = session.getData("latitude")
                            binding.tvLang.text = session.getData("longitude")


//                            Toast.makeText(
//                                activity,
//                                "" + session.getData("latitude"),
//                                Toast.LENGTH_SHORT
//                            ).show()


                        }
                        // Toast.makeText(this,"success",Toast.LENGTH_LONG).show()
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


    private fun gpslocation() {
        if (locationTrack.canGetLocation()) {
            val longitude = locationTrack.getLongitude()
            val latitude = locationTrack.getLatitude()
            Toast.makeText(
                applicationContext, """
     Longitude:${java.lang.Double.toString(longitude)}
     Latitude:${java.lang.Double.toString(latitude)}
     """.trimIndent(), Toast.LENGTH_SHORT
            ).show()
        } else {
            locationTrack.showSettingsAlert()
        }
    }
}