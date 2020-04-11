package com.example.finalprojectapplication.Activities.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import com.example.finalprojectapplication.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;


public class UploadActivity extends AppCompatActivity
{

    //private static final String KEY_

    final static int IMAGE_REQUEST = 0;


    ImageView mImageUpload;
    Button mCancelButton;
    Button mUploadButton;
    EditText mFileName;

    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private FirebaseAuth fAuth;
    private StorageReference fStorageRef;

    private Uri mImageUri;

    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        setupFirebase();

        mUploadButton = findViewById(R.id.uploadButton);
        mCancelButton = findViewById(R.id.cancelButton);
        mImageUpload = findViewById(R.id.imageToUpload);
        mFileName = findViewById(R.id.fileName);


        mImageUpload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openFileChooser();
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                uploadImage();
            }
        });

    }


    private void openFileChooser()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private void uploadImage()
    {
        if (mImageUri != null && mImageUpload.getDrawable() != null)
        {
            final StorageReference imageRef = fStorage.getReference("Images").child(System.currentTimeMillis() + "");//fStorageRef.child("Images/"+System.currentTimeMillis());
            UploadTask uploadTask = imageRef.putFile(mImageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        String name = mFileName.getText().toString();
                        Uri downloadUri = task.getResult();

                        Map<String, Object> image = new HashMap<>();
                        image.put("Name", name);
                        image.put("Uri", downloadUri.toString());

                        DocumentReference db = fStore.collection("data").document(userID)
                                .collection("images").document(userID + "I");
                        db.set(image).addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                System.out.println("Added image succesfully");
                            }
                        }).addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                System.out.println("Not added");
                            }
                        });
                    } else
                    {
                        System.out.println("Didn't upload bro");
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case IMAGE_REQUEST:
                    if (data.getData() == null)
                    {
                        System.out.println("es null bro");
                    } else
                    {
                        mImageUri = data.getData();
                        Picasso.with(UploadActivity.this).load(mImageUri).into(mImageUpload);
                    }
            }
        }
    }


    private void setupFirebase()
    {
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        System.out.println("User:" + userID);
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();
    }
}
