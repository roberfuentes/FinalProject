package com.example.finalprojectapplication.Activities.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.finalprojectapplication.Adapters.FriendRequestAdapter;
import com.example.finalprojectapplication.Model.FriendRequest;
import com.example.finalprojectapplication.Model.User;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FriendRequestActivity extends AppCompatActivity implements FriendRequestAdapter.onRequestListener
{


    RecyclerView mRecyclerView;

    FirestoreRecyclerAdapter adapter;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;

    String currentUID;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        //mToolbar = findViewById(R.id.friend_request_toolbar);
        mRecyclerView = findViewById(R.id.friend_request_recycler);

        //setSupportActionBar(mToolbar);

        /*ActionBar mActionBar= getSupportActionBar();
        //mActionBar.setIcon(R.drawable.ic_left_arrow_filled);
        mActionBar.setTitle("  Friend requests");
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_left_arrow_filled);*/




        setupFirebase();
        setAdapter();

        this.setTitle("Friends requests");


    }

    private void setAdapter(){
        Query query = fStore.collection("friendRequests").document(currentUID)
                .collection("userFriendRequest")
                .whereIn("status", Arrays.asList("pending", "sent"));
                //.whereArrayContains("status", "sent");


        FirestoreRecyclerOptions<FriendRequest> options = new FirestoreRecyclerOptions.Builder<FriendRequest>()
                .setLifecycleOwner(this).setQuery(query, FriendRequest.class).build();

        adapter = new FriendRequestAdapter(options, getApplicationContext(), this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(adapter);


    }

    private void setupFirebase(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        currentUID = fAuth.getCurrentUser().getUid();
    }

    @Override
    public void onClickAccept(String id)
    {
        acceptFriendRequest(id);
    }

    @Override
    public void onClickDecline(int position)
    {
        Toast.makeText(FriendRequestActivity.this, "Workss" + position, Toast.LENGTH_SHORT).show();
    }

    private void acceptFriendRequest(String id){

        final String friendUid = id;
        Toast.makeText(getApplicationContext(), "Friend:"+ friendUid, Toast.LENGTH_SHORT).show();

        DocumentReference documentReference = fStore.collection("friendRequests").document(currentUID).
                collection("userFriendRequest").document(friendUid);

        String status = "accepted";

        Map<String, Object> friendRequestMap = new HashMap<>();
        friendRequestMap.put("status", status);

        documentReference.update(friendRequestMap).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(getApplicationContext(), "Accepted!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(getApplicationContext(), "Error, try again later", Toast.LENGTH_SHORT).show();
            }
        });

        DocumentReference getUserFriend = fStore.collection("users").document(friendUid);

        getUserFriend.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists()){
                        User userFriend = documentSnapshot.toObject(User.class);

                        addFriend(currentUID, userFriend, friendUid);
                        addYourSelfAsAFriend(friendUid);
                    }
                }
            }
        });
    }


    private void addYourSelfAsAFriend(final String friendUid){
        DocumentReference getInfoFromYourself = fStore.collection("users").document(currentUID);

        DocumentReference friendRequestRef = fStore.collection("friendRequests")
                .document(currentUID)
                .collection("userFriendRequest")
                .document(friendUid);

                friendRequestRef.delete().addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        System.out.println("Deleted");
                    }
                });

        getInfoFromYourself.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);

                        addFriend(friendUid, user, currentUID);

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(getApplicationContext(), "Error, try again later", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void addFriend(String firstUid, User userFriend, String friendUid){
        DocumentReference addFriend = fStore.collection("friends").document(firstUid)
                .collection("userFriends").document(friendUid);

        Map<String, Object> friendMap = new HashMap<>();

        friendMap.put("name", userFriend.getName());
        friendMap.put("profilePictureUrl", userFriend.getProfilePictureUrl());
        friendMap.put("status", userFriend.getStatus());
        friendMap.put("uidFriend", friendUid);

        addFriend.set(friendMap).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(getApplicationContext(), "Now both of you are friends", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(getApplicationContext(), "Error, try again later", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemID = item.getItemId();
        switch(itemID){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}
