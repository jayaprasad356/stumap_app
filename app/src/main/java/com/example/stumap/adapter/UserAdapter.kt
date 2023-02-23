package com.example.stumap.adapter

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.stumap.R
import com.example.stumap.activities.MapActivity
import com.example.stumap.helper.ApiConfig
import com.example.stumap.helper.Constant
import com.example.stumap.helper.Session
import com.google.gson.Gson
import com.graymatter.stumap.models.User
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList
import java.util.HashMap


class UserAdapter(
    private val activity: Activity,
    private val userList: List<User>,
    private val type: String,
    private val onFriendRequestSent: () -> Unit // call
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.tvName)
        val email: TextView = itemView.findViewById(R.id.tvemail)
        val id: TextView = itemView.findViewById(R.id.tvid)
        val tvMobile: TextView = itemView.findViewById(R.id.tvMobile)
        val tvlatitide: TextView = itemView.findViewById(R.id.tvlatitide)
        val tvlongitude: TextView = itemView.findViewById(R.id.tvlongitude)
        val btnViewlocation: Button = itemView.findViewById(R.id.btnViewlocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        val session: Session
        session = Session(activity);

        holder.name.text = currentUser.name
        holder.email.text = currentUser.email
        holder.id.text = currentUser.id
        holder.tvMobile.text = currentUser.mobile
        holder.tvlongitude.text = currentUser.longtitude
        holder.tvlatitide.text = currentUser.latitude

        if (type.equals("send")) {
            holder.btnViewlocation.text = "Send Request"
            holder.btnViewlocation.setOnClickListener { view ->
                sendRequest(currentUser, session)
            }
        } else {
            holder.btnViewlocation.setOnClickListener { view ->
                val intent = Intent(view.context, MapActivity::class.java).apply {
                    putExtra(Constant.DESTINATIONLAT, currentUser.latitude)
                    putExtra(Constant.DESTINATIONLNG, currentUser.longtitude)
                }
                view.context.startActivity(intent)
            }
        }
    }

    private fun sendRequest(currentUser: User, session: Session) {

        val params = HashMap<String, String>()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.FRIEND_ID] = currentUser.id
        params[Constant.STATUS] = "0"


        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(activity,jsonObject.getString(Constant.MESSAGE),Toast.LENGTH_SHORT).show()
                        onFriendRequestSent.invoke()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.REQUEST_FRIEND_URL, params, true)
    }

    override fun getItemCount() = userList.size
}
