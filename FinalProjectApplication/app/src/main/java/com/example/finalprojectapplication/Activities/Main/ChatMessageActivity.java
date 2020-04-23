package com.example.finalprojectapplication.Activities.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.finalprojectapplication.Adapters.ChatMessageAdapter;
import com.example.finalprojectapplication.Model.ChatMessage;
import com.example.finalprojectapplication.Model.User;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

public class ChatMessageActivity extends AppCompatActivity
{

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter adapter;
    String currentID;

    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    ActionBar actionBar;
    ImageView mBackArrow;
    ImageView mProfilePicture;
    EditText mFieldMessage;
    ImageButton mSendButton;

    String room;
    String friendID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);


        setupComponents();
        setupFirebase();
        receiveRoom();
        setAdapter();
        getDataFriendUser();


    }

    private void setupComponents(){

        mToolbar = findViewById(R.id.chat_message_toolbar);
        mRecyclerView = findViewById(R.id.chat_message_recycler);
        mBackArrow = findViewById(R.id.chat_message_back_arrow);
        mProfilePicture = findViewById(R.id.chat_message_profile_picture);
        mFieldMessage = findViewById(R.id.chat_message_field_message);
        mSendButton = findViewById(R.id.chat_message_send_button);


        setSupportActionBar(mToolbar);

        actionBar = getSupportActionBar();


    }

    private void receiveRoom(){
        if(getIntent().getStringExtra("room") != null && getIntent().getStringExtra("friendID") != null){
            room = getIntent().getStringExtra("room");
            friendID = getIntent().getStringExtra("friendID");
        }
    }

    private void setupFirebase(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        currentID = fAuth.getCurrentUser().getUid();
    }

    private void setAdapter(){
        Query query = fStore.collection("messages").document(room)
                .collection("roomMessages").limit(20).orderBy("sentAt", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<ChatMessage> options = new FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class).setLifecycleOwner(this).build();

        adapter = new ChatMessageAdapter(options, currentID, friendID);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mRecyclerView.setAdapter(adapter);
    }

    private void getDataFriendUser(){
        DocumentReference user = fStore.collection("users").document(friendID);

        user.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);
                        setFriendDataInToolbar(user);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(ChatMessageActivity.this, "Couldn't retrieve friend data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setFriendDataInToolbar(User user){
        if(user.getProfilePictureUrl().equals("")){
            mProfilePicture.setImageResource(R.drawable.ic_profile_identity_gray);
        }else{
            Picasso.with(ChatMessageActivity.this).load(user.getProfilePictureUrl()).into(mProfilePicture);
        }
        actionBar.setTitle(user.getName());
    }
}
