package com.example.finalprojectapplication.Activities.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.finalprojectapplication.Activities.Login.LoginActivity;
import com.example.finalprojectapplication.Fragments.ChatFragment;
import com.example.finalprojectapplication.Fragments.HomeFragment;
import com.example.finalprojectapplication.Fragments.ProfileFragment;
import com.example.finalprojectapplication.Model.User;
import com.example.finalprojectapplication.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{

    private static final int REQUEST_IMAGE = 1;

    private static final String TAG = "RegisterActivity";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_AGE = "age";
    private static final String KEY_PROFILE = "profilePictureUrl";
    private static final String KEY_STATUS = "status";
    private static final String KEY_UID = "uid";
    private static final String KEY_IS_GOOGLE_SIGN = "isGoogleSign";



    FirebaseAuth fAuth;
    String currentUID;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;

    final Fragment homeFragment= new HomeFragment();
    final Fragment chatFragment = new ChatFragment();
    final Fragment profileFragment = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();

    Fragment selectedFragment = null;

    BottomNavigationView bottomNavigationView;

    private Uri mFileUri;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavSelect);


        this.setTitle("Save and share your data");
        setupFragments();
        setupFirebase();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavSelect = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
        {
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    fm.beginTransaction().hide(selectedFragment).show(homeFragment).commit();
                    selectedFragment = homeFragment;
                    return true;
                case R.id.nav_chat:
                    fm.beginTransaction().hide(selectedFragment).show(chatFragment).commit();
                    selectedFragment = chatFragment;
                    return true;
                case R.id.nav_profile:
                    fm.beginTransaction().hide(selectedFragment).show(profileFragment).commit();
                    selectedFragment = profileFragment;
                    return true;
                default:
                    return false;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.signOut:
                finish();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
        }
        return true;
    }

    private void setupFragments(){

        fm.beginTransaction().add(R.id.fragment_container, homeFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, chatFragment).hide(chatFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, profileFragment).hide(profileFragment).commit();

        selectedFragment = homeFragment;
    }

    private void setupFirebase(){
        fStore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance();
        currentUID = fAuth.getCurrentUser().getUid();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (resultCode == RESULT_OK){
            if(data.getData() == null){
            Log.i("MainActivity", "Uri's file is null");
            return;
            }

            mFileUri = data.getData();
            if(resultCode == RESULT_OK){
                switch(requestCode){
                    case REQUEST_IMAGE:
                        uploadImage();
                        break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage(){
        Toast.makeText(MainActivity.this, "Let's upload!", Toast.LENGTH_SHORT).show();
        if(mFileUri == null){
            return;
        }

        final StorageReference imageRef = fStorage.getReference("PROFILE_PICTURE").child(System.currentTimeMillis() + "");

        UploadTask uploadTask = imageRef.putFile(mFileUri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
        {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
            {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>()
        {
            @Override
            public void onComplete(@NonNull Task<Uri> task)
            {
                userToSetPictureProfile(task.getResult().toString());
                ImageView mProfilePicture
                        =profileFragment.getView().findViewById(R.id.profilePicture);
                Picasso.with(MainActivity.this).load(task.getResult()).into(mProfilePicture);
            }
        });
    }

    private void userToSetPictureProfile(final String url){
        //Get user information;
        DocumentReference userRef = fStore.collection("users").document(currentUID);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);
                        setPictureProfile(url, user);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(MainActivity.this, "Sorry, picture couldn't be set, try again later", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setPictureProfile(String url, User user){
        DocumentReference documentReference = fStore.collection("users").document(currentUID);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put(KEY_NAME, user.getName());
        userMap.put(KEY_EMAIL, user.getEmail());
        userMap.put(KEY_PASSWORD, user.getPassword());
        userMap.put(KEY_LOCATION, user.getLocation());
        userMap.put(KEY_AGE, user.getAge());
        userMap.put(KEY_PROFILE, url);
        userMap.put(KEY_UID, user.getUid());
        userMap.put(KEY_STATUS, user.getStatus());
        userMap.put(KEY_IS_GOOGLE_SIGN, user.getIsGoogleSign());


        documentReference.set(userMap).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Toast.makeText(MainActivity.this, "Your profile picture has been updated!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(MainActivity.this, "Your profile picture couldn't be updated, try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(LoginActivity.isGoogleSign){
                LoginActivity.mGoogleSignIn.signOut();
            }else{
                fAuth.signOut();
            }
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }
}
