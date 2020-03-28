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

public class RegisterActivity extends AppCompatActivity
{

    private String TAG = "RegisterActivity";

    private FirebaseAuth mAuth;

    EditText mEmail, mPassword, mPasswordRepeat;
    Button mBtnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.etEmail);
        mPassword = findViewById(R.id.etPassword);
        mPasswordRepeat = findViewById(R.id.etPasswordRepeat);

        mBtnSignUp = findViewById(R.id.btnSignUp);

        mBtnSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                registerAccount();
            }
        });


    }



    private boolean checkFields(String email, String password, String passwordRepeat){
        if(!email.equals("") && !password.equals("") && !passwordRepeat.equals("")){
            if(password.equals(passwordRepeat)){
                return true;
            }else{
                Toast.makeText(RegisterActivity.this,"Check if passwords are the same", Toast.LENGTH_SHORT);
            }
        }else{
            Toast.makeText(RegisterActivity.this,"Fields can't be empty", Toast.LENGTH_SHORT);
        }
        return false;
    }

    private void registerAccount(){
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        String passwordRepeat = mPasswordRepeat.getText().toString();

        if(checkFields(email, password, passwordRepeat)){
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Succesful!", Toast.LENGTH_SHORT).show();

                        goToMainActivity();
                    }else{
                        Log.w(TAG, "createUserWithEmailAddress:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }
    }

    private void goToMainActivity(){
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }

}
