package com.example.finalprojectapplication.Activities.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;


import com.example.finalprojectapplication.Adapters.ShareAdapter;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShareFileActivity extends AppCompatActivity implements ShareAdapter.onShareListener
{

    private static final String KEY_UID= "fromUid";
    private static final String KEY_SENTAT= "sentAt";
    private static final String KEY_MESSAGE= "messageText";
    private static final String KEY_URL= "url";
    private static final String KEY_TYPE= "type";
    private static final String KEY_ISFILE= "isFile";

    private static final String KEY_NAME= "name";
    private static final String KEY_PICTURE= "picture";
    private static final String KEY_ROOM = "room";
    private static final String KEY_UID_CHAT = "uid";

    RecyclerView mRecyclerView;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter adapter;
    String currentUserID;

    String url;
    String type;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_file);

        mRecyclerView = findViewById(R.id.share_file_recycler);

        setupFirebase();

        if (getIntent() != null)
        {
            type = getIntent().getStringExtra("type");
            url = getIntent().getStringExtra("url");
            Toast.makeText(ShareFileActivity.this, "We got the intent", Toast.LENGTH_SHORT).show();


        } else
        {
            Toast.makeText(ShareFileActivity.this, "Couldn't get it", Toast.LENGTH_SHORT).show();
        }
        setAdapter();
    }

    private void setAdapter(){
        Query query = fStore.collection("friends").document(currentUserID)
                .collection("userFriends");

        FirestoreRecyclerOptions<Friend> options = new FirestoreRecyclerOptions.Builder<Friend>().setLifecycleOwner(this)
                .setQuery(query, Friend.class).build();

        adapter = new ShareAdapter(options, ShareFileActivity.this, this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.setAdapter(adapter);
    }

    private void setupFirebase(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        currentUserID = fAuth.getCurrentUser().getUid();
    }

    @Override
    public void onFriendClick(final String friendID)
    {
        Query query = fStore.collection("chats")
                .document(currentUserID)
                .collection("userChat").whereEqualTo("uid", friendID);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if(querySnapshot!=null){
                        System.out.println("NO ES NULL");
                        if(!querySnapshot.isEmpty()){
                            System.out.println("NO ES EMPTY");
                            List<Chat> rooms = querySnapshot.toObjects(Chat.class);
                            for(Chat chat: rooms){
                                String room = chat.getRoom();
                                setMessage(room);
                            }
                        }else{
                            createChat(friendID);
                        }
                    }

                }
            }
        });
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

                        DocumentReference chatRef = fStore.collection("chats").document(currentUserID)
                                .collection("userChat").document();


                        Map<String, Object> chatMap = new HashMap<>();
                        chatMap.put(KEY_NAME, user.getName());
                        chatMap.put(KEY_PICTURE, user.getProfilePictureUrl());
                        chatMap.put(KEY_ROOM, chatRef.getId());
                        chatMap.put(KEY_UID_CHAT, friendID);

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
                                Toast.makeText(ShareFileActivity.this, "We couldn't add user data to create a room", Toast.LENGTH_SHORT).show();

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
                Toast.makeText(ShareFileActivity.this, "We couldn't retrieve the user data to create a room", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createChatFriend(final String friendID){
        final Query query = fStore.collection("chats").document(currentUserID)
                .collection("userChat").whereEqualTo("uid", friendID);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                QuerySnapshot querySnapshot = task.getResult();
                Toast.makeText(ShareFileActivity.this, "Is empty?" + querySnapshot.isEmpty(), Toast.LENGTH_SHORT).show();
                if(!querySnapshot.isEmpty()){
                    List<DocumentSnapshot> documentSnapshots= querySnapshot.getDocuments();
                    System.out.println("Size document:" + documentSnapshots.size());
                    if(documentSnapshots.size()==1){
                        for(DocumentSnapshot documentSnapshot:documentSnapshots){
                            Chat chat = documentSnapshot.toObject(Chat.class);
                            final String chatID = chat.getRoom();

                            DocumentReference userRef = fStore.collection("users").document(currentUserID);

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
                                            chatMap.put(KEY_UID, currentUserID);
                                            chatMap.put(KEY_ROOM, chatID);
                                            chatMap.put(KEY_PICTURE, user.getProfilePictureUrl());

                                            chatRef.set(chatMap).addOnSuccessListener(new OnSuccessListener<Void>()
                                            {
                                                @Override
                                                public void onSuccess(Void aVoid)
                                                {
                                                    setMessage(chatID);
                                                }
                                            }).addOnFailureListener(new OnFailureListener()
                                            {
                                                @Override
                                                public void onFailure(@NonNull Exception e)
                                                {
                                                    Toast.makeText(ShareFileActivity.this, "We couldn't add user data to create a room", Toast.LENGTH_SHORT).show();

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
                                    Toast.makeText(ShareFileActivity.this, "We couldn't retrieve the user data to create a room", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }

                }
            }
        });
    }

    private void setMessage(String room){
        String message = "File:"+System.currentTimeMillis();
        if(url != null){
            if(!message.equals("")){
                DocumentReference messageReference = fStore.collection("messages").document(room)
                        .collection("roomMessages").document();

                Map<String, Object> messageMap = new HashMap<>();
                messageMap.put(KEY_UID, currentUserID);
                messageMap.put(KEY_MESSAGE, message);
                messageMap.put(KEY_SENTAT, new Date());
                messageMap.put(KEY_ISFILE, true);
                messageMap.put(KEY_URL, url);
                messageMap.put(KEY_TYPE, type);
                messageReference.set(messageMap).addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Toast.makeText(ShareFileActivity.this, "File has been shared!", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(ShareFileActivity.this, "Message couldn't be sent, try again later", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
