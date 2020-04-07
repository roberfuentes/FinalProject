package com.example.finalprojectapplication.Activities.Main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectapplication.Activities.Login.LoginActivity;
import com.example.finalprojectapplication.Fragments.ListFragment;
import com.example.finalprojectapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity
{

    /*FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;*/
    final Fragment listFragment = new ListFragment();
    final FragmentManager fm = getSupportFragmentManager();

    //TextView txtId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fm.beginTransaction().add(R.id.fragment_container, listFragment).commit();







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
