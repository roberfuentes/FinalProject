package com.example.finalprojectapplication.Activities.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.finalprojectapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity
{

    private FirebaseAuth mAuth;

    Button mBtnRegisterEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Firebase
        mAuth = FirebaseAuth.getInstance();


        //Views
        mBtnRegisterEmail = findViewById(R.id.btnRegisterEmail);




        //Go to Activity register with email and password
        mBtnRegisterEmail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goToRegisterActivity();
            }
        });


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

    protected void goToRegisterActivity(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
