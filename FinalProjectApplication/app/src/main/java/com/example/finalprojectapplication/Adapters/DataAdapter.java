package com.example.finalprojectapplication.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectapplication.Activities.Main.UploadActivity;
import com.example.finalprojectapplication.Model.Data;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class DataAdapter extends FirestoreRecyclerAdapter<Data, DataAdapter.DataHolder>{

    private Context mContext;
    private OnFileListener onFileListener;
    FirebaseFirestore fStore;
    FirebaseAuth fAuth;



    public DataAdapter(@NonNull FirestoreRecyclerOptions<Data> options, Context context, OnFileListener onFileListener, FirebaseFirestore fStore, FirebaseAuth fAuth)
    {
        super(options);
        this.mContext= context;
        this.onFileListener = onFileListener;
        this.fStore = fStore;
        this.fAuth = fAuth;
    }





    @Override
    protected void onBindViewHolder(@NonNull DataHolder holder, int position, @NonNull Data model)
    {

        holder.singleFileName.setText(model.getName());
        System.out.println(model.getUrl());

        setTypePicture(model.getType(), holder, model);

        //Glide.with(mContext).load(model.getUri()).dontAnimate().into(holder.singleFile);

    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_file, parent, false);
        return new DataHolder(v);
    }




    public class DataHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public TextView singleFileName;
        public ImageView singleFile;


        public DataHolder(View itemView)
        {
            super(itemView);

            singleFileName = itemView.findViewById(R.id.singleFileName);
            singleFile = itemView.findViewById(R.id.singleFile);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            onFileListener.onFileClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v)
        {
            onFileListener.onFileLongClickListener(getAdapterPosition());
            return true;
        }
    }


    private void setTypePicture(String type, DataHolder holder, Data model){
        switch(type){
            case "image":
                Picasso.with(mContext).load(model.getUrl()).fit().centerCrop().into(holder.singleFile);
                break;
            case "pdf":
                holder.singleFile.setImageResource(R.drawable.ic_picture_as_pdf_outlined);
                break;
            case "audio":
                holder.singleFile.setImageResource(R.drawable.ic_audio_outlined);
                break;
            case "video":
                holder.singleFile.setImageResource(R.drawable.ic_video_outlined);
                break;
        }
    }


    public interface OnFileListener{
        void onFileClick(int position);
        void onFileLongClickListener(int position);
    }


    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }
    public void getInfoItem(int position){
        System.out.println("Id:"+getSnapshots().getSnapshot(position).getReference().getId());


        DocumentReference itemRef = getSnapshots().getSnapshot(position).getReference();
        itemRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        Data data = documentSnapshot.toObject(Data.class);
                        System.out.println(data.getName());
                        System.out.println(data.getUrl());

                    }
                }
            }
        });







    }

}
