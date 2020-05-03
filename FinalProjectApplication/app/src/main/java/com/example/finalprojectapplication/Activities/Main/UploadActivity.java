package com.example.finalprojectapplication.Activities.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.finalprojectapplication.Keys.RequestKey;
import com.example.finalprojectapplication.Model.FileDetail;
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

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class UploadActivity extends AppCompatActivity
{
    private ImageView mImageUpload;
    private Button mCancelButton;
    private Button mUploadButton;
    private EditText mFileName;


    private FirebaseFirestore fStore;
    private FirebaseStorage fStorage;
    private FirebaseAuth fAuth;

    private Uri mFileUri;
    private CharSequence[] chooseType = new CharSequence[]{
            "Image",
            "Pdf",
            "Audio",
            "Video"
    };
    private String userID;

    private RequestKey requestKey;


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

        requestKey = new RequestKey();

        this.setTitle("Upload a file");


        setupComponents();
        setupListeners();


    }
    private void buildDialogFileChooser()
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

    private void openFileChooser(int choose)
    {
        switch (choose)
        {
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

    private void openImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestKey.REQUEST_IMAGE);
    }


    private void openPdf()
    {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestKey.REQUEST_PDF);
    }

    private void openAudio()
    {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestKey.REQUEST_AUDIO);
    }

    private void openVideo()
    {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestKey.REQUEST_VIDEO);
    }

    private void setFileDetailsUI(int codeFile, Intent data)
    {
        if (data.getData() == null)
        {
            Log.i("UploadActivity", "Uri's file is null");
            return;
        }
        mFileUri = data.getData();

        if(requestKey.REQUEST_IMAGE == codeFile){
            Picasso.with(UploadActivity.this).load(mFileUri).into(mImageUpload);
        }else if(requestKey.REQUEST_PDF == codeFile){
            mImageUpload.setImageResource(R.drawable.ic_picture_as_pdf_outlined);
            setWidthHeight(null, null, mImageUpload);
        }else if(requestKey.REQUEST_AUDIO == codeFile){
            mImageUpload.setImageResource(R.drawable.ic_audio_outlined);
            setWidthHeight(null, null, mImageUpload);
        }else if(requestKey.REQUEST_VIDEO == codeFile){
            mImageUpload.setImageResource(R.drawable.ic_video_outlined);
            setWidthHeight(null, null, mImageUpload);
        }
        FileDetail fileDetail = getFileDetails(data.getData());
        mFileName.setText(fileDetail.getName());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            if(requestKey.REQUEST_IMAGE == requestCode){
                setFileDetailsUI(requestKey.REQUEST_IMAGE, data);
            }else if(requestKey.REQUEST_PDF == requestCode){
                setFileDetailsUI(requestKey.REQUEST_PDF, data);
            }else if(requestKey.REQUEST_AUDIO == requestCode){
                setFileDetailsUI(requestKey.REQUEST_AUDIO, data);
            }else if(requestKey.REQUEST_VIDEO == requestCode){
                setFileDetailsUI(requestKey.REQUEST_VIDEO, data);
            }
        }
    }


    private void uploadFile()
    {
        if (mFileUri != null && mImageUpload.getDrawable() != null)
        {
            final FileDetail fileDetail = getFileDetails(mFileUri);
            String type = fileDetail.getType();


            final StorageReference imageRef = fStorage.getReference(type.substring(0, 1).toUpperCase()).child(System.currentTimeMillis() + "");
            UploadTask uploadTask = imageRef.putFile(mFileUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
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

                        Map<String, Object> file = new HashMap<>();
                        file.put("name", name);
                        file.put("type", fileDetail.getType());
                        file.put("size", fileDetail.getSize());
                        file.put("url", downloadUri.toString());


                        DocumentReference db = fStore.collection("data").document(userID)
                                .collection("userData").document();
                        db.set(file).addOnSuccessListener(new OnSuccessListener<Void>()
                        {
                            @Override
                            public void onSuccess(Void aVoid)
                            {
                                Toast.makeText(getApplicationContext(), "File added succesfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UploadActivity.this, MainActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener()
                        {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Toast.makeText(getApplicationContext(), "There was an error in the upload, try again later", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else
                    {
                        Toast.makeText(getApplicationContext(), "There was an error in the upload, try again later", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(UploadActivity.this, "Please, press on the cloud and add a file to upload", Toast.LENGTH_SHORT).show();
        }
    }


    @TargetApi(26)
    private FileDetail getFileDetails(Uri uri)
    {
        FileDetail fileDetail = null;
        if (uri == null)
        {
            return null;
        } else
        {
            fileDetail = new FileDetail();

            if (ContentResolver.SCHEME_FILE.equals(uri.getScheme()))
            {
                File file = new File(uri.getPath());
                fileDetail.setName(file.getName());
                fileDetail.setSize(file.length());
            } else if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()))
            {
                Cursor cursor = getApplicationContext().getContentResolver().query(uri, null, null, null);
                if (cursor != null && cursor.moveToFirst())
                {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                    fileDetail.setName(cursor.getString(nameIndex));
                    fileDetail.setSize(cursor.getLong(sizeIndex));
                }
            }
            String type = getMimeType(uri);
            if (type != null)
            {
                fileDetail.setType(type);
                cutType(fileDetail);
            }
        }
        return fileDetail;
    }

    //Stackoverflow code
    private String getMimeType(Uri uri)
    {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT))
        {
            ContentResolver cr = getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else
        {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private void cutType(FileDetail fileDetail)
    {

        String type = fileDetail.getType();
        String typeFiveCharacters = type.substring(0, 5);


        if (typeFiveCharacters.equals("image"))
        {
            fileDetail.setType("image");
            System.out.println("setimage");
        } else if (typeFiveCharacters.equals("video"))
        {
            System.out.println("setvideo");
            fileDetail.setType("video");
        } else if (typeFiveCharacters.equals("audio"))
        {
            System.out.println("setaudio");
            fileDetail.setType("audio");
        } else if (type.substring(type.length() - 3).equals("pdf"))
        {
            System.out.println("setpdf");
            fileDetail.setType("pdf");
        }
    }


    //SETUPP
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
                buildDialogFileChooser();
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                uploadFile();

            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });


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
}
