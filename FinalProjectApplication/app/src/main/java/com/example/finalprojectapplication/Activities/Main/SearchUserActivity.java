package com.example.finalprojectapplication.Activities.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.finalprojectapplication.Adapters.SearchUserAdapter;
import com.example.finalprojectapplication.Keys.ChatKey;
import com.example.finalprojectapplication.Keys.FriendRequestKey;
import com.example.finalprojectapplication.Model.Friend;
import com.example.finalprojectapplication.Model.FriendRequest;
import com.example.finalprojectapplication.Model.User;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchUserActivity extends AppCompatActivity implements SearchUserAdapter.OnClickUser
{
    private SearchUserAdapter adapter;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private String currentUID;

    private SearchView mSearchView;

    private RecyclerView mRecyclerView;
    private ArrayList<String> currentFriends;
    private ArrayList<String> currentFriendRequests;
    private ArrayList<String> currentFriendRequestsStatus;

    private FriendRequestKey friendRequestKey;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        friendRequestKey = new FriendRequestKey();

        mRecyclerView = findViewById(R.id.search_user_recycler);
        currentFriends = new ArrayList<>();
        currentFriendRequests = new ArrayList<>();
        currentFriendRequestsStatus = new ArrayList<>();

        setupFirebase();
        setUsers();
        this.setTitle("Add friends");
    }

    private void setupFirebase(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        currentUID = fAuth.getCurrentUser().getUid();
    }

    private void setUsers(){
        Query query = fStore.collection("users");

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>().setLifecycleOwner(this)
                .setQuery(query, User.class).build();

        getFriends(options);
    }

    private void getFriends(final FirestoreRecyclerOptions<User> options){
        fStore.collection("friends")
                .document(currentUID)
                .collection("userFriends")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshots = task.getResult();

                    if(!querySnapshots.isEmpty() && querySnapshots.size() != 0){
                        List<Friend> friends = querySnapshots.toObjects(Friend.class);
                        Toast.makeText(getApplicationContext(), "Size:" + friends.size(), Toast.LENGTH_SHORT).show();
                        for(Friend friend: friends){
                            currentFriends.add(friend.getUidFriend());
                        }
                    }
                    fStore.collection("friendRequests")
                            .document(currentUID)
                            .collection("userFriendRequest")
                            .whereIn("status", Arrays.asList("sent", "pending"))
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task)
                        {
                            if(task.isSuccessful())
                            {
                                QuerySnapshot querySnapshots = task.getResult();
                                Toast.makeText(getApplicationContext(), querySnapshots.isEmpty()+"", Toast.LENGTH_SHORT).show();
                                if (!querySnapshots.isEmpty() && querySnapshots.size() != 0)
                                {
                                    List<FriendRequest> friendRequests = querySnapshots.toObjects(FriendRequest.class);
                                    Toast.makeText(getApplicationContext(), "Size:" + friendRequests.size(), Toast.LENGTH_SHORT).show();
                                    for (FriendRequest friendRequest: friendRequests)
                                    {
                                        currentFriendRequests.add(friendRequest.getFromUid());
                                        currentFriendRequestsStatus.add(friendRequest.getStatus());
                                    }
                                }
                                currentFriends.add(currentUID);
                                adapter = new SearchUserAdapter(options, SearchUserActivity.this, fStore, currentFriends,currentFriendRequests, currentFriendRequestsStatus, SearchUserActivity.this, currentUID);

                                mRecyclerView.setLayoutManager(new LinearLayoutManager(SearchUserActivity.this));

                                mRecyclerView.setAdapter(adapter);
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.search_toolbar, menu);

        MenuItem menuItem = menu.findItem(R.id.searcher_search_user);
        mSearchView = (SearchView) menuItem.getActionView();

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String searchQuery)
            {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                Query query = fStore.collection("users").orderBy("name").startAt(newText).endAt(newText+"\uf8ff");

                FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                        .setLifecycleOwner(SearchUserActivity.this)
                        .setQuery(query, User.class)
                        .build();

                getFriends(options);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClickAddUser(String userFriendID)
    {
        sentFriendRequest(userFriendID);
    }

    public void sentFriendRequest(final String userFriendID){

        final DocumentReference userIDRef= fStore.collection("users")
                .document(userFriendID);

        userIDRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);
                        addStatusFriendRequestToCurrentUser(user, userFriendID);
                    }
                }
            }
        });

        final DocumentReference currentUserID = fStore.collection("users")
                .document(currentUID);

        currentUserID.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);
                        sendFriendRequest(user, userFriendID);
                    }
                }
            }
        });
    }

    private void addStatusFriendRequestToCurrentUser(User user, String userFriendID){

        DocumentReference friendRequestRef = fStore.collection("friendRequests")
            .document(currentUID).collection("userFriendRequest")
            .document(user.getUid());

        Map<String, Object> friendRequestMap = new HashMap<>();

        friendRequestMap.put(friendRequestKey.KEY_NAME, user.getName());
        friendRequestMap.put(friendRequestKey.KEY_STATUS, "sent");
        friendRequestMap.put(friendRequestKey.KEY_PROFILE_PICTURE_URL, user.getProfilePictureUrl());
        friendRequestMap.put(friendRequestKey.KEY_UID, user.getUid());

        friendRequestRef.set(friendRequestMap).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {

            }
        });
    }

    private void sendFriendRequest(User user, String userFriendID){
        DocumentReference friendRequestRef = fStore.collection("friendRequests")
                .document(userFriendID).collection("userFriendRequest")
                .document(currentUID);

        Map<String, Object> friendRequestMap = new HashMap<>();

        friendRequestMap.put(friendRequestKey.KEY_NAME, user.getName());
        friendRequestMap.put(friendRequestKey.KEY_STATUS, "pending");
        friendRequestMap.put(friendRequestKey.KEY_UID, user.getUid());
        friendRequestMap.put(friendRequestKey.KEY_PROFILE_PICTURE_URL, user.getProfilePictureUrl());

        friendRequestRef.set(friendRequestMap).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(getApplicationContext(), "Friend request has been sent!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
