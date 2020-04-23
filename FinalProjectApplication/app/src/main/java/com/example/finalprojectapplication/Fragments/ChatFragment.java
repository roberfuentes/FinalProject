package com.example.finalprojectapplication.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.finalprojectapplication.Activities.Main.ChatMessageActivity;
import com.example.finalprojectapplication.Activities.Main.FriendListActivity;
import com.example.finalprojectapplication.Activities.Main.FriendRequestActivity;
import com.example.finalprojectapplication.Adapters.ChatAdapter;
import com.example.finalprojectapplication.Model.Chat;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements  View.OnClickListener, ChatAdapter.OnClickChat
{

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    FirestoreRecyclerAdapter adapter;


    View v;
    String currentUserID;

    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    FloatingActionButton floatingActionButtonOptions, floatingActionButtonListFriends, floatingActionButtonAddFriends;

    public ChatFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        return getLayoutInflater().inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        v = view;
        setupFirebase();
        setupComponents();
        setupListeners();
        animateFloatings();
        setAdapter();
    }

    private void setupFirebase(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        currentUserID = fAuth.getCurrentUser().getUid();
    }


    private void setAdapter(){
        Query query = fStore.collection("chats").document(currentUserID).collection("userChat");
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setLifecycleOwner(getActivity()).setQuery(query, Chat.class).build();

        adapter = new ChatAdapter(options, getContext(), this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(adapter);
    }

    private void setupComponents(){


        mToolbar = v.findViewById(R.id.chat_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        mRecyclerView = v.findViewById(R.id.chat_recycler_list);
        floatingActionButtonOptions = v.findViewById(R.id.floating_button_options);
        floatingActionButtonListFriends = v.findViewById(R.id.floating_button_list_friends);
        floatingActionButtonAddFriends = v.findViewById(R.id.floating_button_add_friends);
    }

    private void animateFloatings(){
        floatingActionButtonListFriends.animate().translationY(floatingActionButtonListFriends.getHeight()).setDuration(2000);

        floatingActionButtonAddFriends.animate().translationY(floatingActionButtonListFriends.getHeight());
    }

    private void setupListeners(){
        floatingActionButtonAddFriends.setOnClickListener(this);
        floatingActionButtonListFriends.setOnClickListener(this);
        floatingActionButtonOptions.setOnClickListener(this);
        mRecyclerView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int viewID = v.getId();

        switch(viewID){
            case R.id.floating_button_options:
                visibilityFloatingButtons();
                break;
            case R.id.floating_button_add_friends:
                goToFriendRequestActivity();
                break;
            case R.id.floating_button_list_friends:
                goToFriendsList();
                break;
        }
    }

    private void visibilityFloatingButtons(){
        if(floatingActionButtonListFriends.getVisibility() == View.INVISIBLE){
            floatingActionButtonListFriends.setVisibility(View.VISIBLE);
            floatingActionButtonAddFriends.setVisibility(View.VISIBLE);
        }else{
            floatingActionButtonListFriends.setVisibility(View.INVISIBLE);
            floatingActionButtonAddFriends.setVisibility(View.INVISIBLE);
        }
    }

    private void goToFriendRequestActivity(){
        Intent friendRequest = new Intent(getContext(), FriendRequestActivity.class);
        startActivity(friendRequest);
    }

    private void goToFriendsList(){
        Intent friendList = new Intent(getContext(), FriendListActivity.class);
        startActivity(friendList);
    }

    @Override
    public void onClickChat(String chatID)
    {
        getFriendID(chatID);
    }

    private void getFriendID(final String chatID){
        DocumentReference chatRef = fStore.collection("chats").document(currentUserID)
                .collection("userChat").document(chatID);
        chatRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        Chat chat = documentSnapshot.toObject(Chat.class);
                        String friendID = chat.getUid();
                        goToChatMessageActivity(chatID, friendID);
                    }
                }
            }
        });
    }

    private void goToChatMessageActivity(String chatID, String friendID){
        Intent intent = new Intent(getContext(), ChatMessageActivity.class);
        intent.putExtra("room", chatID);
        intent.putExtra("friendID", friendID);

        startActivity(intent);
    }
}
