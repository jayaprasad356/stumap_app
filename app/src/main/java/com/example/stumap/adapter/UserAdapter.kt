package com.example.stumap.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stumap.databinding.UserLayoutBinding
import com.graymatter.stumap.models.User

class UserAdapter(private val userList: ArrayList<User>?) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private lateinit var binding: UserLayoutBinding

    inner class ViewHolder(binding: UserLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = UserLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }
    override fun getItemCount(): Int = userList?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = userList!![position] as User?
        with(model) {
            binding.tvid.text = this?.id
            binding.tvName.text = this?.name ?: ""
            binding.tvemail.text = this?.email ?: ""
            binding.tvMobile.text = this?.mobile ?: ""
            binding.tvlatitide.text = this?.latitude ?: ""
            binding.tvlongitude.text = this?.longtitude ?: ""
        }
    }
}