package com.example.finalprojectapplication.Activities.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.finalprojectapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
{

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();



    }


    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser current = mAuth.getCurrentUser();
        if(current == null){
            Toast.makeText(this, "There's no user my G", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, current.getUid(), Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "hello", Toast.LENGTH_SHORT);
    }
}
