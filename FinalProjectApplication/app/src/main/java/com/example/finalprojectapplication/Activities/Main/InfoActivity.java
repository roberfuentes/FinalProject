package com.example.finalprojectapplication.Activities.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectapplication.Model.Data;
import com.example.finalprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class InfoActivity extends AppCompatActivity
{

    ImageView mInfoPicture;
    TextView mInfoName, mInfoExtraSize, mInfoExtraType;
    EditText mInfoEditName;

    CardView mInfoCardView, mInfoEditableCardView;


    FirebaseFirestore fStore;
    FirebaseAuth fAuth;


    private Toolbar mToolbarEdit;
    Menu menu;

    Data data;
    String urlData;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        mInfoPicture= findViewById(R.id.infoPicture);
        mInfoName = findViewById(R.id.infoName);
        mInfoExtraSize = findViewById(R.id.infoExtraSize);
        mInfoExtraType = findViewById(R.id.infoExtraType);
        mInfoEditName = findViewById(R.id.infoEditableName);

        mInfoCardView = findViewById(R.id.infoCardView);
        mInfoEditableCardView = findViewById(R.id.infoEditableCardView);

        mToolbarEdit = findViewById(R.id.info_editToolbar);

        setSupportActionBar(mToolbarEdit);

        setupFirebase();
        setupInfoData();



    }



    public boolean getInfoDataBundle(){
        data = getIntent().getExtras().getParcelable("data");

            if(data == null){
                urlData = getIntent().getStringExtra("fileRef");
                if(urlData == null){
                    return false;
                }else{
                    return true;
                }

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
                                if(data.getType().equals("image")){
                                    Picasso.with(InfoActivity.this).load(data.getUrl()).into(mInfoPicture);
                                }else if(data.getType().equals("pdf")){
                                    mInfoPicture.setImageResource(R.drawable.ic_picture_as_pdf_outlined);
                                }else if(data.getType().equals("video")){
                                    mInfoPicture.setImageResource(R.drawable.ic_video_outlined);
                                }else if(data.getType().equals("audio")){
                                    mInfoPicture.setImageResource(R.drawable.ic_audio_outlined);
                                }


                                mInfoName.setText(data.getName());
                                mInfoExtraType.setText(data.getType());
                                mInfoExtraSize.setText(data.getSize()/1000 +" MB");

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

    public DocumentReference getInfoDataFromCloud()
    {
        if (data != null)
        {
            DocumentReference fileRef = fStore.collection("data").document(fAuth.getCurrentUser().getUid()).collection("userData").document(data.getUrl());
            return fileRef;
        } else
        {
            DocumentReference fileRef = fStore.collection("data").document(fAuth.getCurrentUser().getUid()).collection("userData").document(urlData);
            return fileRef;
        }
    }

    public void setupFirebase(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.editable_toolbar, menu);
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()){
            case R.id.edit_pen_action:
                activeEditMode();
                break;
            case R.id.check_edit_action:
                updateData();
                break;
            case R.id.cancel_edit_action:
                containerInTextMode();
                break;
        }
        return false;
    }

    private void activeEditMode(){
        menu.findItem(R.id.edit_pen_action).setVisible(false);
        menu.findItem(R.id.check_edit_action).setVisible(true);
        menu.findItem(R.id.cancel_edit_action).setVisible(true);


        containerInEditMode();
        mInfoEditableCardView.setVisibility(View.VISIBLE);
        mInfoCardView.setVisibility(View.INVISIBLE);


    }

    private void containerInEditMode(){
        mInfoEditName.setText(mInfoName.getText());
    }

    private void containerInTextMode(){
        menu.findItem(R.id.edit_pen_action).setVisible(true);
        menu.findItem(R.id.check_edit_action).setVisible(false);
        menu.findItem(R.id.cancel_edit_action).setVisible(false);

        mInfoCardView.setVisibility(View.VISIBLE);
        mInfoEditableCardView.setVisibility(View.INVISIBLE);
    }

    private void updateData(){

        updateDataInCloud();
    }

    private void updateDataInCloud(){
        final String name = mInfoEditName.getText().toString();
        if(name.equals("")){
            Toast.makeText(InfoActivity.this, "Field can't be empty", Toast.LENGTH_SHORT).show();
        }else{
            Map<String, Object> info = new HashMap<>();
            info.put("name", name);

            if(data!=null || urlData!=null){
                DocumentReference fileRef = getInfoDataFromCloud();
                fileRef.update(info).addOnSuccessListener(new OnSuccessListener<Void>()
                {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Toast.makeText(InfoActivity.this, "File updated", Toast.LENGTH_SHORT).show();
                        mInfoName.setText(name);
                        containerInTextMode();
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(InfoActivity.this, "File couldn't be updated", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if((keyCode == event.KEYCODE_BACK)){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
