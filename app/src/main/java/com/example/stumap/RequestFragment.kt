package com.example.stumap

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stumap.databinding.FragmentRequestBinding
import com.example.stumap.helper.ApiConfig
import com.example.stumap.helper.Constant
import com.example.stumap.helper.Session
import com.google.gson.Gson
import com.graymatter.stumap.models.User
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap

class RequestFragment : Fragment() {
    lateinit var binding: FragmentRequestBinding
    lateinit var activity: Activity;
    lateinit var session: Session
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRequestBinding.inflate(inflater, container, false)
        activity= requireActivity()
        session=Session(activity)
        showRequests()

        binding!!.recycler.setLayoutManager(
            LinearLayoutManager(
                activity,
                LinearLayoutManager.VERTICAL,
                false
            )
        )



        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    private fun showRequests() {
        val params = HashMap<String, String>()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
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
                        val adapter = RequestAdapter(activity, userList,)
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
        }, activity, Constant.MY_REQUESTS_URL, params, true)

    }
}