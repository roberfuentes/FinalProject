package com.example.finalprojectapplication.Activities.Main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.finalprojectapplication.Adapters.FriendAdapter;
import com.example.finalprojectapplication.Model.Friend;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FriendListActivity extends AppCompatActivity
{

    RecyclerView mRecyclerView;


    FirestoreRecyclerAdapter adapter;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    String currentUID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        mRecyclerView = findViewById(R.id.friend_list_recycler);

        setupFirebase();
        setAdapter();

    }

    private void setupFirebase(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        currentUID = fAuth.getCurrentUser().getUid();
    }

    private void setAdapter(){

        Query query = fStore.collection("friends").document(currentUID).collection("userFriends");

        FirestoreRecyclerOptions<Friend> options = new FirestoreRecyclerOptions.Builder<Friend>().setLifecycleOwner(this)
                .setQuery(query, Friend.class).build();

        adapter = new FriendAdapter(options, this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(adapter);




    }



}
