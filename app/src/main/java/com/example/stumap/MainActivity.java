package com.example.stumap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.example.stumap.adapter.UserAdapter;
import com.example.stumap.model.User;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    UserAdapter userAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recycler = findViewById(R.id.recycler);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recycler.setLayoutManager(linearLayoutManager);

        user();
    }

    private void user() {

        ArrayList<User> users = new ArrayList<>();
        User user = new User("","Tamil","63820****8","0.1111111","0.1111111");



        users.add(user);



        userAdapter = new UserAdapter(MainActivity.this,users);
        recycler.setAdapter(userAdapter);



    }
}