package com.example.finalprojectapplication.Fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.finalprojectapplication.Activities.Main.UploadActivity;
import com.example.finalprojectapplication.Model.User;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment
{

    private static final String TAG = "HomeFragment";
    private static final int REQUEST_STORAGE_WRITE_READ = 1;


    //Firebase Cloud
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private FirestoreRecyclerAdapter adapter;
    FirebaseStorage mStorageRef;
    private DatabaseReference mDatabaseRef;



    private RecyclerView recyclerView;
    private View view;

    FloatingActionButton uploadButton;

    public HomeFragment()
    {
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        this.view = view;

        //setup view and data
        setupRecyclerView();
        setupFirebase();
        setupDataInRecycerView();

        //setup Components and listeners
        setComponents();
        setListeners();

    }

    
    
    
    //SETUP
    
    public void setComponents(){
        uploadButton = view.findViewById(R.id.uploadButton);


    }

    public void setListeners(){
        uploadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                
                
                openUploadDialog();

            }
        });
    }
    

    public void setupRecyclerView(){
        //Recycler
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    private void setupFirebase(){
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();


    }



    public void setupDataInRecycerView(){

        Log.i(TAG, "This is the current UID" + userID);

        //Query
        Query query = fStore.collection("users");

        //RecyclerOptions
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                    .setQuery(query, User.class)
                    .build();

        //RecyclerAdapter
        adapter = new FirestoreRecyclerAdapter<User, UserViewHolder>(options)
        {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
            {
                System.out.println("view holder");
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_file, parent, false);
                return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model)
            {
                holder.listName.setText(model.getName());

            }
        };

        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        System.out.println("work");


    }

    private class UserViewHolder extends RecyclerView.ViewHolder{

        private TextView listName;
        public UserViewHolder(@NonNull View itemView)
        {
            super(itemView);

            listName = itemView.findViewById(R.id.listName);
        }
    }
    
    
    
    
    
    //LifeCycle

    @Override
    public void onStart()
    {
        super.onStart();
        System.out.println("Started");
        adapter.startListening();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        System.out.println("Stopped");
        adapter.stopListening();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        adapter.startListening();
    }

    
    
    
    //Permissionsç
    private void openUploadDialog(){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(ContextCompat.checkSelfPermission(getContext(), permissions[0])  == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED){


            Intent intent = new Intent(getActivity(), UploadActivity.class);
            startActivity(intent);
            //new UploadDialog(getActivity());  Dialog Abandoned

        }else{
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_STORAGE_WRITE_READ);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == REQUEST_STORAGE_WRITE_READ){
            openUploadDialog();
        }

    }
}
