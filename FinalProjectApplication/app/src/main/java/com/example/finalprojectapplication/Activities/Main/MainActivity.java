package com.example.finalprojectapplication.Activities.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.finalprojectapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
{

    FirebaseAuth mAuth;

    TextView txtId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        txtId = findViewById(R.id.txtID);

        if(mAuth.getCurrentUser() != null){
            txtId.setText("LOGGED!" + mAuth.getCurrentUser().getUid() + " and your EMAIL is: " + mAuth.getCurrentUser().getEmail());
        }else{
            txtId.setText("you are not logged my G :(");

        }


    }
}
