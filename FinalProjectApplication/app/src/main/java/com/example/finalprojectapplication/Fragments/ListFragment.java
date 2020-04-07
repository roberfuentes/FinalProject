package com.example.finalprojectapplication.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.finalprojectapplication.Model.User;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment
{
    //Firebase
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private FirestoreRecyclerAdapter adapter;


    private RecyclerView recyclerView;
    private View view;

    public ListFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        this.view = view;
        setupRecyclerView();
        setupFirebase();
        loadData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


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



    public void loadData(){

        System.out.println("El uid es este:" + userID);

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
                System.out.println("view holder 2");
                System.out.println("Works name: " + model.getName());
                holder.listName.setText(model.getName());

            }
        };

        recyclerView.setHasFixedSize(true);
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

    /*@Override
    public void onResume()
    {
        super.onResume();
        System.out.println("On resume");
    }*/
}
