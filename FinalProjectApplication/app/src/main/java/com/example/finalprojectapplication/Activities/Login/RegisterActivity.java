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
import com.example.finalprojectapplication.Keys.UserKey;
import com.example.finalprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener
{

    private static final String TAG = "RegisterActivity";

    private UserKey userKey;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;


    EditText mName, mEmail, mPassword, mPasswordRepeat;
    Button mBtnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userKey = new UserKey();

        mName = findViewById(R.id.etName);
        mEmail = findViewById(R.id.etEmail);
        mPassword = findViewById(R.id.etPassword);
        mPasswordRepeat = findViewById(R.id.etPasswordRepeat);

        mBtnSignUp = findViewById(R.id.btnSignUp);

        mBtnSignUp.setOnClickListener(this);

        this.setTitle("Sign up");
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId()){
            case R.id.btnSignUp:
                registerAccount();
                break;
        }
    }

    private boolean checkFields(String name, String email, String password, String passwordRepeat)
    {
        if (!name.equals("") && !email.equals("") && !password.equals("") && !passwordRepeat.equals(""))
        {
            if (password.equals(passwordRepeat) && password.length() >= 6)
                {
                return true;
            } else
            {
                if (password.equals(passwordRepeat))
                {
                    Toast.makeText(RegisterActivity.this, "Check if passwords are the same", Toast.LENGTH_SHORT);
                } else
                {
                    Toast.makeText(RegisterActivity.this, "Check if passwords are the same", Toast.LENGTH_SHORT);
                }
            }
        } else
        {
            Toast.makeText(RegisterActivity.this, "Fields can't be empty", Toast.LENGTH_SHORT);
        }
        return false;
    }

    private void registerAccount()
    {
        final String name = mName.getText().toString();
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        String passwordRepeat = mPasswordRepeat.getText().toString();

        if (checkFields(name, email, password, passwordRepeat))
        {
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        Toast.makeText(RegisterActivity.this, "Succesful!", Toast.LENGTH_SHORT).show();

                        saveUserInCloud(name, email, password);
                        goToMainActivity();
                    } else
                    {
                        Log.w(TAG, "createUserWithEmailAddress:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void goToMainActivity()
    {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void saveUserInCloud(String name, String email, String password)
    {
        String userID = fAuth.getCurrentUser().getUid();
        DocumentReference db = fStore.collection("users").document(userID);
        Map<String, Object> user = new HashMap<>();
        user.put(userKey.KEY_NAME, name);
        user.put(userKey.KEY_EMAIL, email);
        user.put(userKey.KEY_PASSWORD, password);
        user.put(userKey.KEY_LOCATION, "");
        user.put(userKey.KEY_AGE, "");
        user.put(userKey.KEY_PROFILE, "");
        user.put(userKey.KEY_UID, userID);
        user.put(userKey.KEY_STATUS, "");
        user.put(userKey.KEY_IS_GOOGLE_SIGN, false);


        System.out.println("There we go");


        db.set(user).addOnSuccessListener(new OnSuccessListener<Void>()
        {
            @Override
            public void onSuccess(Void aVoid)
            {
                Log.i(TAG, "Added succesfully!");
                Toast.makeText(RegisterActivity.this, "Added", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(RegisterActivity.this, "NOT ADDED", Toast.LENGTH_SHORT).show();

                Log.w(TAG, "User data hasn't been added!");
            }
        });

    }

}
