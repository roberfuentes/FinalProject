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
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShareAdapter extends FirestoreRecyclerAdapter<Friend, ShareAdapter.ShareDataHolder>
{


    Context mContext;
    ShareAdapter.onShareListener onShareListener;

    public ShareAdapter(@NonNull FirestoreRecyclerOptions<Friend> options, Context context, ShareAdapter.onShareListener onShareListener)
    {
        super(options);
        this.mContext = context;
        this.onShareListener = onShareListener;

    }

    @NonNull
    @Override
    public ShareDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.share_file_single_file, parent, false);
        return new ShareDataHolder(view);
    }




    @Override
    protected void onBindViewHolder(@NonNull ShareDataHolder holder, int position, @NonNull Friend model)
    {

        holder.mName.setText(model.getName());

        if(model.getProfilePictureUrl().equals("")){
            holder.mProfilePicture.setImageResource(R.drawable.ic_profile_identity_gray);
        }else{
            Picasso.with(mContext).load(model.getProfilePictureUrl()).into(holder.mProfilePicture);
        }
    }

    public class ShareDataHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mProfilePicture;
        TextView mName;




        public ShareDataHolder(@NonNull View itemView)
        {
            super(itemView);
            mProfilePicture = itemView.findViewById(R.id.share_file_profile_picture);
            mName = itemView.findViewById(R.id.share_file_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            onShareListener.onFriendClick(getSnapshots().getSnapshot(getAdapterPosition()).getReference().getId());
        }
    }

    public interface onShareListener{
        void onFriendClick(String friendID);
    }
}
