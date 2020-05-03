package com.example.finalprojectapplication.Adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectapplication.Activities.Main.FileViewerActivity;
import com.example.finalprojectapplication.Activities.Main.ShareFileActivity;
import com.example.finalprojectapplication.Model.Data;
import com.example.finalprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;



import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class DataAdapter extends FirestoreRecyclerAdapter<Data, DataAdapter.DataHolder>{

    private Context mContext;
    private OnFileListener onFileListener;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private FirebaseStorage fStorage;

    private ArrayList<String> fileUrl = new ArrayList<>();
    private ArrayList<String> fileType = new ArrayList<>();


    public DataAdapter(@NonNull FirestoreRecyclerOptions<Data> options, Context context, OnFileListener onFileListener, FirebaseFirestore fStore, FirebaseAuth fAuth, FirebaseStorage fStorage)
    {
        super(options);
        this.mContext= context;
        this.onFileListener = onFileListener;
        this.fStore = fStore;
        this.fAuth = fAuth;
        this.fStorage = fStorage;
    }





    @Override
    protected void onBindViewHolder(@NonNull DataHolder holder, int position, @NonNull Data model)
    {

        holder.singleFileName.setText(model.getName());
        System.out.println(model.getUrl());
        fileUrl.add(model.getUrl());
        fileType.add(model.getType());

        setTypePicture(model.getType(), holder, model);
    }

    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_file, parent, false);
        return new DataHolder(v);
    }



    public class DataHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView singleFileName;
        private ImageView singleFile;


        private DataHolder(View itemView)
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


    public void deleteFile(int position){
        getSnapshots().getSnapshot(position).getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(mContext, "Data has been deleted from Database", Toast.LENGTH_SHORT).show();
            }
        });
        String url = fileUrl.get(position);
        fStorage.getReferenceFromUrl(url).delete().addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(mContext, "Data has been deleted from Storage", Toast.LENGTH_SHORT).show();
            }
        });
        fileUrl.remove(position);
        fileType.remove(position);



    }


    public DocumentReference getInfoFile(int position){
        return getSnapshots().getSnapshot(position).getReference();
    }

    public void downloadUrl(int position){
        DocumentReference fileRef = getInfoFile(position);

        fileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        Data data = documentSnapshot.toObject(Data.class);
                        String type = data.getType();
                        switch(type){
                            case "image":
                                //StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                //storageReference = storageReference.child("images/"+data.getUrl());
                                downloadFile(mContext, data.getName(), Environment.DIRECTORY_DOWNLOADS, data.getUrl());
                        }

                    }
                }
            }
        });
    }

    private void downloadFile(Context context, String fileName, String destinationDirectory, String url){

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName);

        System.out.println("Downloading");
        downloadManager.enqueue(request);
    }


    public void readFile(int position){
        String type = fileType.get(position);
        String url = fileUrl.get(position);

        if(type==null || url == null || type.equals("") || url.equals("")){
            Toast.makeText(mContext, "Couldn't retrieve the file try again later", Toast.LENGTH_SHORT).show();
        }else{
            fileUrl.clear();
            fileType.clear();

            DocumentReference fileRef = getInfoFile(position);

            Intent intent = new Intent(mContext, FileViewerActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("type", type);
            intent.putExtra("fileRef", fileRef.getId());
            intent.putExtra("toolbar", "expose");


            mContext.startActivity(intent);
        }
    }

    public void shareFile(int position){
        String type = fileType.get(position);
        String url = fileUrl.get(position);

        if(type==null || url == null || type.equals("") || url.equals("")){
            Toast.makeText(mContext, "Couldn't retrieve the file try again later", Toast.LENGTH_SHORT).show();
        }else{
            fileUrl.clear();
            fileType.clear();
            
            Intent intent = new Intent(mContext, ShareFileActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("type", type);
            mContext.startActivity(intent);
        }
    }





}
