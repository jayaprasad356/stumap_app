package com.example.stumap

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stumap.adapter.UserAdapter
import com.example.stumap.databinding.FragmentHomeBinding
import com.example.stumap.databinding.FragmentMyFriendsBinding
import com.example.stumap.helper.ApiConfig
import com.example.stumap.helper.Constant
import com.example.stumap.helper.Session
import com.google.gson.Gson
import com.graymatter.stumap.models.User
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap


class MyFriendsFragment : Fragment() {
    private var _binding : FragmentMyFriendsBinding? = null
    var activity: Activity? = null
    lateinit var session: Session


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyFriendsBinding.inflate(inflater, container, false)
        activity= requireActivity()
        session = Session(getActivity())
        _binding!!.recycler.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL,
            false
        )
        myFriendsList()

        return _binding!!.root
    }
    private fun myFriendsList() {
        val params = HashMap<String, String>()
        params[Constant.USER_ID]=session.getData(Constant.USER_ID)
        //   params[Constant.SEARCH] = search
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
                        val adapter = getActivity()?.let { UserAdapter(it, userList,"view"){} }
                        _binding!!.recycler.setAdapter(adapter)
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
        }, activity, Constant.MY_FRIENDLIST_URL, params, true)

    }
}