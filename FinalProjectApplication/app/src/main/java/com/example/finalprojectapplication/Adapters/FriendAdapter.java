package com.example.finalprojectapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalprojectapplication.Model.Friend;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FriendAdapter extends FirestoreRecyclerAdapter<Friend, FriendAdapter.FriendDataHolder>
{


    Context mContext;
    onFriendListener onFriendListener;

    public FriendAdapter(@NonNull FirestoreRecyclerOptions<Friend> options, Context context, onFriendListener onFriendListener)
    {
        super(options);
        this.mContext = context;
        this.onFriendListener = onFriendListener;
    }

    @NonNull
    @Override
    public FriendDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_single_file, parent, false);
        return new FriendDataHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull FriendDataHolder holder, int position, @NonNull Friend model)
    {

        holder.mFriendListName.setText(model.getName());
        holder.mFriendListStatus.setText(model.getStatus());
        System.out.println("status"+model.getStatus());
        if(model.getProfilePictureUrl().equals("")){
            holder.mFriendListProfilePicture.setImageResource(R.drawable.ic_profile_identity_gray);
        }else{
            Picasso.with(mContext).load(model.getProfilePictureUrl()).into(holder.mFriendListProfilePicture);
        }
    }

    public class FriendDataHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mFriendListProfilePicture;
        TextView mFriendListName;
        TextView mFriendListStatus;



        public FriendDataHolder(@NonNull View itemView)
        {
            super(itemView);
            mFriendListProfilePicture = itemView.findViewById(R.id.friend_list_profile_picture);
            mFriendListName = itemView.findViewById(R.id.friend_list_name);
            mFriendListStatus = itemView.findViewById(R.id.friend_list_status);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            onFriendListener.onFriendClick(getSnapshots().getSnapshot(getAdapterPosition()).getReference().getId());
        }
    }

    public interface onFriendListener{
        void onFriendClick(String friendID);
    }

}
