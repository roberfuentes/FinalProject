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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ChatMessageAdapter extends FirestoreRecyclerAdapter<ChatMessage, ChatMessageAdapter.ChatMessageHolder>
{

    String currentID;
    String friendID;
    public ChatMessageAdapter(@NonNull FirestoreRecyclerOptions<ChatMessage> options, String currentID, String friendID)
    {
        super(options);
        this.currentID = currentID;
        this.friendID = friendID;
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
            //holder.mBackgroundText.setBackgroundColor(Color.parseColor("#7FDBFF"));

        }else{
            holder.mBackgroundUserFriend.setVisibility(View.VISIBLE);
            holder.mFriendUserText.setText(model.getMessageText());
            //holder.mBackgroundText.setBackgroundColor(Color.parseColor("#0074D9"));
        }
    }

    public class ChatMessageHolder extends RecyclerView.ViewHolder{
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
        }
    }
}
