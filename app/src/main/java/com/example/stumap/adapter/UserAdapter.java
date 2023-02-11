package com.example.stumap.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.stumap.R;
import com.example.stumap.model.User;


import java.util.ArrayList;


public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    final Activity activity;
    final ArrayList<User> users;
    public UserAdapter(Activity activity, ArrayList<User> users) {
        this.activity = activity;
        this.users = users;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.user_layout, parent, false);
        return new ExploreItemHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderParent, int position) {
        final ExploreItemHolder holder = (ExploreItemHolder) holderParent;
        final User user = users.get(position);


         holder.tvName.setText(user.getName());
         holder.tvLatitude.setText(user.getLotitude());
         holder.tvLogtidue.setText(user.getLongtidue());
         holder.tvMobile.setText(user.getMobile());



    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ExploreItemHolder extends RecyclerView.ViewHolder {


        final TextView tvName,tvMobile,tvLatitude,tvLogtidue;
        public ExploreItemHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvLogtidue = itemView.findViewById(R.id.tvLogtidue);
            tvLatitude = itemView.findViewById(R.id.tvLatitude);
            tvMobile = itemView.findViewById(R.id.tvMobile);



        }
    }
}

