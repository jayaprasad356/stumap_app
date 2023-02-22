package com.example.stumap

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stumap.adapter.UserAdapter
import com.example.stumap.databinding.FragmentFindFriendsBinding
import com.example.stumap.helper.ApiConfig
import com.example.stumap.helper.Constant
import com.example.stumap.helper.Session
import com.google.gson.Gson
import com.graymatter.stumap.models.User
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap


class FindFriendsFragment : Fragment() {
    lateinit var binding: FragmentFindFriendsBinding
    lateinit var activity: Activity
    lateinit var session: Session
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFindFriendsBinding.inflate(inflater, container, false)
        activity = requireActivity()
        session = Session(activity)
        findFriendsApi("")

        binding.recycler.layoutManager = LinearLayoutManager(
            activity,
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.edSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (binding.edSearch.text.length == 10) {
                    val mobile = binding.edSearch.text.toString()
                    findFriendsApi(mobile)
                } else {
                    findFriendsApi("")
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root

    }

    @SuppressLint("SuspiciousIndentation")
    private fun findFriendsApi(mobile: String) {
        val params = HashMap<String, String>()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.SEARCH] = mobile
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
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
                        val adapter = UserAdapter(activity, userList, "send")
                        binding.recycler.adapter = adapter
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
}