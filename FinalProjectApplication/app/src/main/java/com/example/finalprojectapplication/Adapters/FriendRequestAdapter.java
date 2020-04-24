package com.example.finalprojectapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalprojectapplication.Model.FriendRequest;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FriendRequestAdapter extends FirestoreRecyclerAdapter<FriendRequest, FriendRequestAdapter.FriendRequestHolder>
{

    Context mContext;
    onRequestListener onRequestListener;


    public FriendRequestAdapter(@NonNull FirestoreRecyclerOptions<FriendRequest> options, Context mContext, onRequestListener onRequestListener)
    {
        super(options);
        this.mContext = mContext;
        this.onRequestListener = onRequestListener;
    }

    @NonNull
    @Override
    public FriendRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_single_file, parent, false);
        return new FriendRequestHolder(view);
    }
    @Override
    protected void onBindViewHolder(@NonNull FriendRequestHolder holder, int position, @NonNull FriendRequest model)
    {
        String picture = model.getProfilePictureUrl();
        if(picture.equals("")){
            holder.mFriendRequestProfile.setImageResource(R.drawable.ic_profile_identity_gray);
        }else{
            Picasso.with(mContext).load(model.getProfilePictureUrl()).into(holder.mFriendRequestProfile);
        }

        holder.mFriendRequestName.setText(model.getName());


        String status = model.getStatus();
        if(status.equals("sent")){
            holder.mFriendRequestSent.setVisibility(View.VISIBLE);
            holder.mFriendRequestSent.setEnabled(false);
            holder.mFriendRequestStatus.setText("Status: SENT");
            holder.mFriendRequestAccept.setVisibility(View.GONE);
            holder.mFriendRequestDecline.setVisibility(View.GONE);
        }else{
            holder.mFriendRequestStatus.setText("Status: PENDING");
        }
    }

    public class FriendRequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mFriendRequestProfile;
        TextView mFriendRequestName;
        Button mFriendRequestAccept;
        Button mFriendRequestDecline;
        Button mFriendRequestSent;
        TextView mFriendRequestStatus;


        public FriendRequestHolder(@NonNull View itemView)
        {
            super(itemView);

            mFriendRequestProfile = itemView.findViewById(R.id.friend_request_picture);
            mFriendRequestName = itemView.findViewById(R.id.friend_request_name);
            mFriendRequestAccept = itemView.findViewById(R.id.friend_request_accept_button);
            mFriendRequestDecline = itemView.findViewById(R.id.friend_request_decline_button);
            mFriendRequestSent = itemView.findViewById(R.id.friend_request_sent_button);
            mFriendRequestStatus = itemView.findViewById(R.id.friend_request_status);

            mFriendRequestAccept.setOnClickListener(this);
            mFriendRequestDecline.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            onRequestListener.onClickAccept(getSnapshots().getSnapshot(getAdapterPosition()).getReference().getId());
            onRequestListener.onClickDecline(getAdapterPosition());
        }
    }

    public interface onRequestListener{
        void onClickAccept(String id);
        void onClickDecline(int position);
    }


}
