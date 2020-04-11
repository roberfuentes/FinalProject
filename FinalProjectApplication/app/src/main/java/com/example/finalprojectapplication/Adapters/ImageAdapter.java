package com.example.finalprojectapplication.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.finalprojectapplication.Model.Image;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class ImageAdapter extends FirestoreRecyclerAdapter<Image, ImageAdapter.ImageHolder>{

    private Context mContext;
    public ImageAdapter(@NonNull FirestoreRecyclerOptions<Image> options, Context context)
    {
        super(options);
        this.mContext= context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ImageHolder holder, int position, @NonNull Image model)
    {

        holder.singleFileName.setText(model.getName());
        System.out.println(model.getUri());
        Picasso.with(mContext).load(model.getUri()).fit().centerCrop().into(holder.singleFile);
        //Glide.with(mContext).load(model.getUri()).dontAnimate().into(holder.singleFile);

    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_file, parent, false);
        return new ImageHolder(v);
    }

    class ImageHolder extends RecyclerView.ViewHolder{
        public TextView singleFileName;
        public ImageView singleFile;


        public ImageHolder(View itemView)
        {
            super(itemView);

            singleFileName = itemView.findViewById(R.id.singleFileName);
            singleFile = itemView.findViewById(R.id.singleFile);


        }
    }

}
