package com.example.finalprojectapplication.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.finalprojectapplication.Model.ChatMessage;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatMessageAdapter extends FirestoreRecyclerAdapter<ChatMessage, ChatMessageAdapter.ChatMessageHolder>
{

    String currentID;
    String friendID;
    List<String> url = new ArrayList<>();
    List<String> type = new ArrayList<>();

    OnMessageListener onMessageListener;

    public ChatMessageAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options, String currentID, String friendID, OnMessageListener onMessageListener)
    {
        super(options);
        this.currentID = currentID;
        this.friendID = friendID;
        this.onMessageListener = onMessageListener;
    }

    @NonNull
    @Override
    public ChatMessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_single_file, parent, false);
        return new ChatMessageHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatMessageHolder holder, int position, @NonNull ChatMessage model)
    {

        if(model.getFromUid().equals(currentID)){
            holder.mBackgroundCurrentUser.setVisibility(View.VISIBLE);
            holder.mCurrentUserText.setText(model.getMessageText());
            if(model.getIsFile()){
                holder.mCurrentUserText.setTextColor(Color.parseColor("#cd00cd"));
            }
            //holder.mBackgroundText.setBackgroundColor(Color.parseColor("#7FDBFF"));

        }else{
            holder.mBackgroundUserFriend.setVisibility(View.VISIBLE);
            holder.mFriendUserText.setText(model.getMessageText());
            if(model.getIsFile()){
                holder.mFriendUserText.setTextColor(Color.parseColor("#cd00cd"));
            }
            //holder.mBackgroundText.setBackgroundColor(Color.parseColor("#0074D9"));
        }

        url.add(model.getUrl());
        type.add(model.getType());



    }

    public class ChatMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        LinearLayout mBackgroundCurrentUser, mBackgroundUserFriend;
        TextView mCurrentUserText, mFriendUserText;
        public ChatMessageHolder(@NonNull View itemView)
        {
            super(itemView);
            System.out.println("goes through holder");

            mCurrentUserText = itemView.findViewById(R.id.chat_message_currentUser_text);
            mFriendUserText = itemView.findViewById(R.id.chat_message_userFriend_text);

            mBackgroundCurrentUser = itemView.findViewById(R.id.chat_message_currentUser_backgroundText);
            mBackgroundUserFriend = itemView.findViewById(R.id.chat_message_friendUserbackgroundText);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            onMessageListener.onMessageClick(url.get(getAdapterPosition()), type.get(getAdapterPosition()));
        }
    }

    public interface OnMessageListener{
        void onMessageClick(String url, String type);
    }
}
