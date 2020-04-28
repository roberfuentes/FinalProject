package com.example.finalprojectapplication.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectapplication.Model.User;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SearchUserAdapter extends FirestoreRecyclerAdapter<User, SearchUserAdapter.SearchUserHolder>
{
    FirebaseFirestore fStore;
    Context mContext;
    ArrayList<String> currentFriends;
    OnClickUser onClickUser;
    ArrayList<String> currentFriendRequests;
    ArrayList<String> currentFriendRequestsStatus;
    String currentUID;

    public SearchUserAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context mContext,
                             FirebaseFirestore fStore, ArrayList<String> currentFriends, ArrayList<String> currentFriendRequests,  ArrayList<String> currentFriendRequestsStatus, OnClickUser onClickUser, String currentUID)
    {
        super(options);
        this.fStore = fStore;
        this.currentFriends = currentFriends;
        this.onClickUser = onClickUser;
        this.currentFriendRequests = currentFriendRequests;
        this.currentFriendRequestsStatus = currentFriendRequestsStatus;
        this.currentUID = currentUID;
        this.mContext = mContext;
    }

    public SearchUserAdapter(@NonNull FirestoreRecyclerOptions<User> options, FirebaseFirestore fStore, Context mContext)
    {
        super(options);
        this.fStore = fStore;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SearchUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_search_user_single_file, parent, false);
        return new SearchUserHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull SearchUserHolder holder, int position, @NonNull User model)
    {
        System.out.println("currentFriends" + currentFriends);
        System.out.println("Checking if you go through here");
        //Profile Picture
        String profilePicture = model.getProfilePictureUrl();
        if(profilePicture.equals("")){
            holder.mProfilePicture.setImageResource(R.drawable.ic_profile_identity_gray);
        }else{
            Picasso.with(mContext).load(model.getProfilePictureUrl()).into(holder.mProfilePicture);
        }
        //Name
        holder.mName.setText(model.getName());


        //Button
        if(currentFriends != null){
            int result = currentFriends.indexOf(model.getUid());
            System.out.println("result" + result);
            if(result == -1){
                System.out.println("check again" + result);
                result = currentFriendRequests.indexOf(model.getUid());
                if(result != -1 && currentFriendRequests != null && currentFriendRequestsStatus != null){
                    String status = currentFriendRequestsStatus.get(result);
                    holder.mAddButton.setText(status.toUpperCase());
                    holder.mAddButton.setEnabled(false);
                    holder.mAddButton.setBackgroundColor(Color.parseColor("#888888"));
                }
            }else{
                if(currentUID.equals(model.getUid()))
                {
                    holder.mAddButton.setText("YOU");
                    holder.mAddButton.setEnabled(false);
                    holder.mAddButton.setBackgroundColor(Color.parseColor("#888888"));
                }else{
                    System.out.println("This user should be dispalyed" + model.getName());
                    holder.mAddButton.setText("ADDED");
                    holder.mAddButton.setEnabled(false);
                    holder.mAddButton.setBackgroundColor(Color.parseColor("#888888"));
                }
            }
        }

    }


    public class SearchUserHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mProfilePicture;
        TextView mName;
        Button mAddButton;

        public SearchUserHolder(@NonNull View itemView)
        {
            super(itemView);

            mProfilePicture = itemView.findViewById(R.id.friend_search_picture);
            mName = itemView.findViewById(R.id.friend_search_name);
            mAddButton = itemView.findViewById(R.id.friend_search_add_button);
            mAddButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            onClickUser.onClickAddUser(getSnapshots().getSnapshot(getAdapterPosition()).getReference().getId());
        }
    }

    public interface OnClickUser{
        void onClickAddUser(String userID);
    }



}
