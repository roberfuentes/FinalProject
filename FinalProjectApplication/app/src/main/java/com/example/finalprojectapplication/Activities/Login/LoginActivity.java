package com.example.finalprojectapplication.Activities.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalprojectapplication.Activities.Main.MainActivity;
import com.example.finalprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity
{

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;

    EditText mEtEmail, mEtPassword;

    Button mBtnRegisterEmail;
    Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Firebase
        mAuth = FirebaseAuth.getInstance();


        //Views
        mEtEmail = findViewById(R.id.etEmail);
        mEtPassword = findViewById(R.id.etPassword);
        mEtEmail.setText("rober97frr@gmail.com");
        mEtPassword.setText("123456");

        mBtnLogin = findViewById(R.id.btnLogin);
        mBtnRegisterEmail = findViewById(R.id.btnRegisterEmail);


        //BUTTON ON CLICK LISTENERS

        //Sign in
        mBtnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                login();
            }
        });



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




    private void login(){
        String email = mEtEmail.getText().toString();
        String password = mEtPassword.getText().toString();

        if(checkFields(email, password)){
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Logged in!", Toast.LENGTH_SHORT).show();
                        goToMainActivity();
                    }else{
                        Toast.makeText(LoginActivity.this, "Credentials are not valid!", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                    }
                }
            });
        }

    }


    //INTENTS

    private void goToMainActivity(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void goToRegisterActivity(){
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }





    private boolean checkFields(String email, String password){
        if(!email.equals("") && !password.equals("")){
            return true;
        }else{
            Toast.makeText(LoginActivity.this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

}
