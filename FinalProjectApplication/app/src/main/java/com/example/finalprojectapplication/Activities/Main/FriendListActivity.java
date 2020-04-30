package com.example.finalprojectapplication.Activities.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.finalprojectapplication.Adapters.FriendAdapter;
import com.example.finalprojectapplication.Model.Chat;
import com.example.finalprojectapplication.Model.Friend;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendListActivity extends AppCompatActivity implements FriendAdapter.onFriendListener
{

    private static final String KEY_NAME= "name";
    private static final String KEY_PICTURE= "picture";
    private static final String KEY_ROOM = "room";
    private static final String KEY_UID = "uid";


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
        setContentView(R.layout.activity_friend_list);

        //mToolbar = findViewById(R.id.friend_list_toolbar);
        /*setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Friends list");*/


        mRecyclerView = findViewById(R.id.friend_list_recycler);

        setupFirebase();
        setAdapter();

        this.setTitle("Friend list");
    }

    private void setupFirebase(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        currentUID = fAuth.getCurrentUser().getUid();
    }

    private void setAdapter(){

        Query query = fStore.collection("friends").document(currentUID)
                .collection("userFriends");

        FirestoreRecyclerOptions<Friend> options = new FirestoreRecyclerOptions.Builder<Friend>().setLifecycleOwner(this)
                .setQuery(query, Friend.class).build();

        adapter = new FriendAdapter(options, this, this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onFriendClick(String friendID)
    {
        checkIfChat(friendID);
    }

    private void checkIfChat(final String friendID){

        final Query query = fStore.collection("chats").document(currentUID)
                .collection("userChat").whereEqualTo("uid", friendID);

        fStore.collection("chats").document(currentUID)
                .collection("userChat").whereEqualTo("uid", friendID).
                        addSnapshotListener(new EventListener<QuerySnapshot>()
                {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
                    {
                        if (queryDocumentSnapshots!=null){
                            if(queryDocumentSnapshots.size() > 1){
                                Toast.makeText(FriendListActivity.this, "There was " + queryDocumentSnapshots.size() + " rooms", Toast.LENGTH_SHORT).show();
                            }else if(queryDocumentSnapshots.size() == 0){
                                createChat(friendID);
                            }else{
                                List<DocumentSnapshot>  documentSnapshots= queryDocumentSnapshots.getDocuments();
                                for(DocumentSnapshot documentSnapshot:documentSnapshots){
                                    Chat chat = documentSnapshot.toObject(Chat.class);
                                    goToChat(chat.getRoom(), chat.getUid());
                                }
                            }
                        }

                    }
                });
    }

    private void goToChat(String room, String friendID){
        Intent intent = new Intent(FriendListActivity.this, ChatMessageActivity.class);
        intent.putExtra("room", room);
        intent.putExtra("friendID", friendID);
        startActivity(intent);
    }

    private void createChat(final String friendID){

        //Retrieve data user
        DocumentReference userRef = fStore.collection("users").document(friendID);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);

                        DocumentReference chatRef = fStore.collection("chats").document(currentUID)
                                .collection("userChat").document();


                        Map<String, Object> chatMap = new HashMap<>();
                        chatMap.put(KEY_NAME, user.getName());
                        chatMap.put(KEY_PICTURE, user.getProfilePictureUrl());
                        chatMap.put(KEY_ROOM, chatRef.getId());
                        chatMap.put(KEY_UID, friendID);

                        chatRef.set(chatMap).addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                createChatFriend(friendID);
                            }
                        }).addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Toast.makeText(FriendListActivity.this, "We couldn't add user data to create a room", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(FriendListActivity.this, "We couldn't retrieve the user data to create a room", Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void createChatFriend(final String friendID){
        final Query query = fStore.collection("chats").document(currentUID)
                .collection("userChat").whereEqualTo("uid", friendID);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                QuerySnapshot querySnapshot = task.getResult();
                Toast.makeText(FriendListActivity.this, "Is empty?" + querySnapshot.isEmpty(), Toast.LENGTH_SHORT).show();
                if(!querySnapshot.isEmpty()){
                    List<DocumentSnapshot> documentSnapshots= querySnapshot.getDocuments();
                    System.out.println("Size document:" + documentSnapshots.size());
                    if(documentSnapshots.size()==1){
                        for(DocumentSnapshot documentSnapshot:documentSnapshots){
                            Chat chat = documentSnapshot.toObject(Chat.class);
                            final String chatID = chat.getRoom();

                            DocumentReference userRef = fStore.collection("users").document(currentUID);

                            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot documentSnapshot = task.getResult();
                                        if(documentSnapshot.exists()){
                                            User user = documentSnapshot.toObject(User.class);

                                            DocumentReference chatRef = fStore.collection("chats").document(friendID)
                                                    .collection("userChat").document(chatID);

                                            Map<String, Object> chatMap = new HashMap<>();

                                            chatMap.put(KEY_NAME, user.getName());
                                            chatMap.put(KEY_UID, currentUID);
                                            chatMap.put(KEY_ROOM, chatID);
                                            chatMap.put(KEY_PICTURE, user.getProfilePictureUrl());

                                            chatRef.set(chatMap).addOnSuccessListener(new OnSuccessListener<Void>()
                                            {
                                                @Override
                                                public void onSuccess(Void aVoid)
                                                {

                                                }
                                            }).addOnFailureListener(new OnFailureListener()
                                            {
                                                @Override
                                                public void onFailure(@NonNull Exception e)
                                                {
                                                    Toast.makeText(FriendListActivity.this, "We couldn't add user data to create a room", Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText(FriendListActivity.this, "We couldn't retrieve the user data to create a room", Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                    }

                }
            }
        });
    }


}
