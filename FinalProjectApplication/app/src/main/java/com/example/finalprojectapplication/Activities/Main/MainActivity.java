package com.example.finalprojectapplication.Activities.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.finalprojectapplication.Activities.Login.LoginActivity;
import com.example.finalprojectapplication.Fragments.ChatFragment;
import com.example.finalprojectapplication.Fragments.HomeFragment;
import com.example.finalprojectapplication.Fragments.ProfileFragment;
import com.example.finalprojectapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
{

    /*FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;*/
    final Fragment homeFragment= new HomeFragment();
    final Fragment chatFragment = new ChatFragment();
    final Fragment profileFragment = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();

    Fragment selectedFragment = null;

    BottomNavigationView bottomNavigationView;



    //TextView txtId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavSelect);


        fm.beginTransaction().add(R.id.fragment_container, homeFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, chatFragment).hide(chatFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, profileFragment).hide(profileFragment).commit();
        selectedFragment = homeFragment;



        /*fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        userID = fAuth.getCurrentUser().getUid();
        DocumentReference db = fStore.collection("users").document(userID);
        db.addSnapshotListener(MainActivity.this, new EventListener<DocumentSnapshot>()
        {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e)
            {
                txtId.setText("LOGGED!" + fAuth.getCurrentUser().getUid() + " and your EMAIL is: " + fAuth.getCurrentUser().getEmail() + " " + documentSnapshot.getString("name"));
            }
        });


        txtId = findViewById(R.id.txtID);

        if(fAuth.getCurrentUser() != null){
            Toast.makeText(MainActivity.this,"works", Toast.LENGTH_SHORT).show();

        }else{
            txtId.setText("you are not logged my G :(");

        }*/
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
}
