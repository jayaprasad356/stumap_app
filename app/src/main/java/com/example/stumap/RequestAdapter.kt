package com.example.stumap



import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.stumap.activities.MapActivity
import com.example.stumap.helper.ApiConfig
import com.example.stumap.helper.Constant
import com.example.stumap.helper.Session
import com.graymatter.stumap.models.User
import org.json.JSONObject
import java.util.HashMap


class RequestAdapter(
    private val activity: Activity,
    private val userList: List<User>,
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {


    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.tvName)
        val email: TextView = itemView.findViewById(R.id.tvemail)
        val id: TextView = itemView.findViewById(R.id.tvid)
        val tvMobile: TextView = itemView.findViewById(R.id.tvMobile)
        val tvlatitide: TextView = itemView.findViewById(R.id.tvlatitide)
        val tvlongitude: TextView = itemView.findViewById(R.id.tvlongitude)
        val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        val btnReject: Button = itemView.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.request_accept_layout, parent, false)
        return RequestViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val currentUser = userList[position]
        val session: Session
        session = Session(activity);

        holder.name.text = currentUser.name
        holder.email.text = currentUser.email
        holder.id.text = currentUser.id
        holder.tvMobile.text = currentUser.mobile
        holder.tvlongitude.text = currentUser.longtitude
        holder.tvlatitide.text = currentUser.latitude
        holder.btnReject.setOnClickListener { view ->
            sendRequest(currentUser, session,"2")
        }
        holder.btnAccept.setOnClickListener { view ->
            sendRequest(currentUser, session,"1")
        }
    }


    override fun getItemCount() = userList.size
    private fun sendRequest(currentUser: User, session: Session,status:String) {

        val params = HashMap<String, String>()
        params[Constant.USER_ID] = currentUser.id
        params[Constant.FRIEND_ID] = session.getData(Constant.USER_ID)
        params[Constant.STATUS] =status


        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(activity,jsonObject.getString(Constant.MESSAGE),Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.REQUEST_FRIEND_URL, params, true)
    }
}
