package com.example.stumap.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stumap.MainActivity
import com.example.stumap.R
import com.example.stumap.activities.MapActivity
import com.example.stumap.activities.ProfileActivity
import com.example.stumap.helper.Constant
import com.graymatter.stumap.models.User


class UserAdapter( userList1: MainActivity, private val userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>()

{

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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.name.text = currentUser.name
        holder.email.text = currentUser.email
        holder.id.text = currentUser.id
        holder.tvMobile.text = currentUser.mobile
        holder.tvlongitude.text = currentUser.longtitude
        holder.tvlatitide.text = currentUser.latitude

        holder.btnViewlocation.setOnClickListener { view ->
            val intent = Intent(view.context, MapActivity::class.java).apply {
                putExtra(Constant.DESTINATIONLAT , currentUser.latitude)
                putExtra(Constant.DESTINATIONLNG , currentUser.longtitude)
            }
            view.context.startActivity(intent)
        }

    }

    override fun getItemCount() = userList.size
}
