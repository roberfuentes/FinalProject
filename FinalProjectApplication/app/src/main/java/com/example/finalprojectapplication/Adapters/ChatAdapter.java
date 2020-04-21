package com.example.finalprojectapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalprojectapplication.Model.Chat;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatAdapter extends FirestoreRecyclerAdapter<Chat, ChatAdapter.ChatHolder>
{

    FirestoreRecyclerOptions mOptions;
    Context mContext;

    List<String> uid = new ArrayList<>();




    public ChatAdapter(FirestoreRecyclerOptions<Chat> options, Context context){
        super(options);
        mOptions = options;
        mContext = context;
    }



    @Override
    protected void onBindViewHolder(@NonNull ChatHolder holder, int position, @NonNull Chat model)
    {
        holder.chatName.setText(model.getName());
        if(model.getPicture().equals("")){
            holder.chatPicture.setImageResource(R.drawable.ic_profile_outline);
        }else{
            Picasso.with(mContext).load(model.getPicture()).into(holder.chatPicture);
        }

        uid.add(model.getUid());
        System.out.println("UID SIZE" + uid.size());


    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_single_file, parent, false);
        return new ChatHolder(v);
    }

    public class ChatHolder extends RecyclerView.ViewHolder{

        private TextView chatName;
        private ImageView chatPicture;


        public ChatHolder(@NonNull View itemView)
        {
            super(itemView);
            chatName = itemView.findViewById(R.id.chat_name);
            chatPicture = itemView.findViewById(R.id.chat_picture);

        }
    }
}
