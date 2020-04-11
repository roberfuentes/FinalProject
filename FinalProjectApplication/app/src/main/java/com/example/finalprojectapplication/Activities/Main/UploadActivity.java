package com.example.finalprojectapplication.Activities.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.finalprojectapplication.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

    final static int REQUEST_CONTENT = 0;


    private ImageView mImageUpload;
    private Button mCancelButton;
    private Button mUploadButton;
    private EditText mFileName;

    private Button mBtnTest;

    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private FirebaseAuth fAuth;
    private StorageReference fStorageRef;

    private Uri mImageUri;
    private CharSequence[] chooseType = new CharSequence[]{
            "Image",
            "Pdf",
            "Audio",
            "Video"
    };
    private String userID;



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

        mBtnTest = findViewById(R.id.btnTest);


        setupComponents();
        setupListeners();


    }


    private void openFileChooser(int choose)
    {
        switch(choose){
            case 0:
                openImage();
                break;
            case 1:
                openPdf();
                break;
            case 2:
                openAudio();
                break;
            case 3:
                openVideo();
                break;
        }
    }

    private void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CONTENT);
    }


    private void openPdf(){
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CONTENT);
    }

    private void openAudio(){
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CONTENT);
    }

    private void openVideo(){
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CONTENT);
    }

    private void uploadImage()
    {
        if (mImageUri != null && mImageUpload.getDrawable() != null)
        {
            final StorageReference imageRef = fStorage.getReference("Images").child(System.currentTimeMillis() + "");
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
                        image.put("name", name);
                        image.put("uri", downloadUri.toString());

                        DocumentReference db = fStore.collection("data").document(userID)
                                .collection("images").document();
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
                case REQUEST_CONTENT:
                    if (data.getData() == null)
                    {
                        Log.i("UploadActivity", "Uri's file is null");
                    } else
                    {
                        mImageUri = data.getData();
                        Picasso.with(UploadActivity.this).load(mImageUri).into(mImageUpload);

                        System.out.println("type:"+MimeTypeMap.getFileExtensionFromUrl(mImageUri.toString()));
                        setWidthHeight(null, null, mImageUpload);
                    }
                    break;
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

    private void setupComponents()
    {
        mImageUpload.setImageResource(R.drawable.ic_upload);
        setWidthHeight(null, null, mImageUpload);

    }

    private void setupListeners()
    {

        mImageUpload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //openFileChooser();
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


        mBtnTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(UploadActivity.this);

                builder.setSingleChoiceItems(chooseType, 0, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(UploadActivity.this, "" + which, Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                        openFileChooser(which);

                    }
                });

                builder.show();
            }
        });
    }

    private void getExtension(Uri uri)
    {
        System.out.println("type:"+MimeTypeMap.getFileExtensionFromUrl(uri.toString()));
        getMimeType(uri);

    }

    /**
     * @param height of the View
     * @param width  of the View
     *               <p>
     *               Set null if you want by default 600 by default
     */
    private void setWidthHeight(Integer height, Integer width, View view)
    {
        if (height == null && width == null)
        {
            view.getLayoutParams().height = 600;
            view.getLayoutParams().width = 600;
        } else
        {
            view.getLayoutParams().height = height;
            view.getLayoutParams().width = width;

        }
    }

    public void getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        System.out.println(mimeType);
    }
}
