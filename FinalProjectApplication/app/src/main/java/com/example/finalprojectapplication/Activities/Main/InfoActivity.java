package com.example.finalprojectapplication.Activities.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectapplication.Model.Data;
import com.example.finalprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class InfoActivity extends AppCompatActivity
{

    ImageView mInfoPicture;
    TextView mInfoName, mInfoSize, mInfoType;


    FirebaseFirestore fStore;
    FirebaseAuth fAuth;



    Data data;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mInfoPicture= findViewById(R.id.infoPicture);
        mInfoName = findViewById(R.id.infoName);
        mInfoSize = findViewById(R.id.infoSize);
        mInfoType = findViewById(R.id.infoType);


        setupFirebase();
        setupInfoData();


    }



    public boolean getInfoDataBundle(){
        data = getIntent().getExtras().getParcelable("data");
        if(data.getUrl()== null){
            return false;
        }else{
            return true;
        }
    }
    public void setupInfoData(){
        if(getInfoDataBundle()){

            DocumentReference fileRef = getInfoDataFromCloud();
            fileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
            {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task)
                {
                    if(task.isSuccessful()){
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if(documentSnapshot.exists()){
                            Data data = documentSnapshot.toObject(Data.class);
                            if(data!=null){
                                Picasso.with(InfoActivity.this).load(data.getUrl()).into(mInfoPicture);

                                mInfoName.setText(data.getName());
                                mInfoType.setText(data.getType());
                                mInfoSize.setText(data.getSize()/1000 +" MB");

                                mInfoPicture.getLayoutParams().height = 600;
                                mInfoPicture.getLayoutParams().width = 600;
                            }
                        }
                    }
                }
            });

        }else{
            Toast.makeText(InfoActivity.this, "Couldn't retrieve the information, try again later", Toast.LENGTH_SHORT).show();
        }
    }

    public DocumentReference getInfoDataFromCloud(){
        DocumentReference fileRef = fStore.collection("data").document(fAuth.getCurrentUser().getUid()).collection("userData").document(data.getUrl());
        return fileRef;


    }
    public void setupFirebase(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
    }
}
