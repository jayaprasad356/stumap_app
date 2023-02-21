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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stumap.activities.LoginActivity
import com.example.stumap.activities.ProfileActivity
import com.example.stumap.adapter.UserAdapter
import com.example.stumap.databinding.ActivityMainBinding
import com.example.stumap.databinding.FragmentFindFriendsBinding
import com.example.stumap.databinding.FragmentHomeBinding
import com.example.stumap.extras.LocationTrack
import com.example.stumap.helper.ApiConfig
import com.example.stumap.helper.Constant
import com.example.stumap.helper.Session
import com.google.gson.Gson
import com.graymatter.stumap.models.User
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: UserAdapter
    private lateinit var locationTrack: LocationTrack
    private lateinit var session: Session
    private lateinit var calendar: Calendar
    var activity: Activity? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        activity= requireActivity()
        session = Session(getActivity())
checkPermission()
        user_detail()
      //  gpslocation()


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
            this.set(Calendar.YEAR,Calendar.MONTH,Calendar.DAY_OF_MONTH)
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
            data(search)
        } catch (e: Exception) {
            // Handle the exception here
            // For example, you could log the error message or show an error dialog to the user
        }



        binding!!.ivprofile.setOnClickListener{

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


                if (binding.edSearch.text.length == 10 ){

                    //   Toast.makeText(activity,""+binding.edSearch.text.toString(),Toast.LENGTH_SHORT).show()

                    var search = binding.edSearch.text.toString()
                    data(search)
                }

                else{
                    var search = ""
                    data(search)

                }
            }

            override fun afterTextChanged(s: Editable?) {





            }

        })

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
        return binding.root
    }
    @SuppressLint("SuspiciousIndentation")
    private fun data(search: String) {
        val params = HashMap<String, String>()
        params[Constant.SEARCH] = search
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Log.d("user",response)
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
                        val adapter = getActivity()?.let { UserAdapter(it, userList,"view") }
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
        }, activity, Constant.SEARCH_USER_URL, params, true)


    }
    private fun user_detail() {
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

                        binding.Name.setText(jsonArray.getJSONObject(0).getString(Constant.NAME))
                        binding.mobile.setText(jsonArray.getJSONObject(0).getString(Constant.MOBILE))


                        //TODO : ASSIGN JSONARRAY TO SESSION

                    } else {
                        Toast.makeText(
                            activity,
                            "" + jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    activity,
                    java.lang.String.valueOf(response) + java.lang.String.valueOf(result),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, activity, Constant.USER_DETAIL, params, true)
    }

    private val permissionsResultCallback = registerForActivityResult(
        ActivityResultContracts.RequestPermission()){
        when (it) {
            true -> {  locationTrack = activity?.let { it1 -> LocationTrack(it1.applicationContext) }!!

                if(locationTrack.canGetLocation())  {
                    updateLocationToApi(session.getData(Constant.USER_ID)
                        ,locationTrack.getLatitude().toString()
                        ,locationTrack.getLongitude().toString())



                }else{
                    Toast.makeText(activity,"unable to get Location",Toast.LENGTH_LONG).show()
                }}
            false -> {
                Toast.makeText(activity, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun checkPermission() {
        val permission = activity?.let {
            ContextCompat.checkSelfPermission(
                it.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permission != PackageManager.PERMISSION_GRANTED) {
            permissionsResultCallback.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationTrack = activity?.let { LocationTrack(it.applicationContext) }!!

            if(locationTrack.canGetLocation())  {
                updateLocationToApi(session.getData(Constant.USER_ID)
                    ,locationTrack.getLatitude().toString()
                    ,locationTrack.getLongitude().toString())


                //  Toast.makeText(this@MainActivity,locationTrack.getLatitude().toString(),Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(activity,"unable to get Location",Toast.LENGTH_LONG).show()
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
                            activity,
                            "" + jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                Toast.makeText(
                    activity,
                    java.lang.String.valueOf(response) + java.lang.String.valueOf(result),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, activity, Constant.LOCATION_URL, params, true)
    }
    private fun gpslocation() {
        if (locationTrack.canGetLocation()) {
            val longitude = locationTrack.getLongitude()
            val latitude = locationTrack.getLatitude()
            Toast.makeText(
                activity, """
     Longitude:${java.lang.Double.toString(longitude)}
     Latitude:${java.lang.Double.toString(latitude)}
     """.trimIndent(), Toast.LENGTH_SHORT
            ).show()
        } else {
            locationTrack.showSettingsAlert()
        }
    }
}