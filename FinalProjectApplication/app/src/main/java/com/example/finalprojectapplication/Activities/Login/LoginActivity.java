package com.example.finalprojectapplication.Activities.Login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectapplication.Activities.Main.MainActivity;
import com.example.finalprojectapplication.Model.User;
import com.example.finalprojectapplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1;

    private final String KEY_NAME = "name";
    private final String KEY_EMAIL = "email";
    private final String KEY_PASSWORD = "password";
    private final String KEY_AGE = "age";
    private final String KEY_STATUS = "status";
    private final String KEY_LOCATION = "location";
    private final String KEY_IS_GOOGLE_SIGN = "isGoogleSign";
    private final String KEY_PROFILE = "profilePictureUrl";
    private static final String KEY_UID = "uid";




    private FirebaseAuth mAuth;

    EditText mEtEmail, mEtPassword;

    Button mBtnRegisterEmail, mBtnRegisterGoogleSignUp;
    Button mBtnLogin;
    TextView mTvForgotPassowrd;


    public static GoogleSignInClient mGoogleSignIn;
    public static boolean isGoogleSign = false;
    public static String password = "";

    FirebaseFirestore fStore;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setupComponents();
        this.setTitle("Login");

    }

    private void setupComponents(){
        //Firebase
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        //Views
        mEtEmail = findViewById(R.id.etEmail);
        mEtPassword = findViewById(R.id.etPassword);

        mBtnLogin = findViewById(R.id.btnLogin);
        mBtnRegisterEmail = findViewById(R.id.btnRegisterEmail);
        mBtnRegisterGoogleSignUp = findViewById(R.id.btnRegisterGoogle);
        mTvForgotPassowrd = findViewById(R.id.tvPassword);

        mBtnLogin.setOnClickListener(this);
        mBtnRegisterEmail.setOnClickListener(this);
        mBtnRegisterGoogleSignUp.setOnClickListener(this);
        mTvForgotPassowrd.setOnClickListener(this);

    }


    private void login(){
        String email = mEtEmail.getText().toString();
        final String password = mEtPassword.getText().toString();

        if(checkFields(email, password)){
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if(task.isSuccessful()){
                        LoginActivity.password = password;
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

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        Toast.makeText(LoginActivity.this, id + "", Toast.LENGTH_SHORT).show();

        switch(id){
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnRegisterEmail:
                goToRegisterActivity();
                break;
            case R.id.btnRegisterGoogle:
                Toast.makeText(LoginActivity.this, "click works", Toast.LENGTH_SHORT).show();
                createRequest();
                break;
            case R.id.tvPassword:
                forgotPassword();
                break;

        }
    }

    private void createRequest(){

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignIn = GoogleSignIn.getClient(LoginActivity.this, gso);

        signIn();
    }

    private void signIn(){
        Intent signIn = mGoogleSignIn.getSignInIntent();
        startActivityForResult(signIn, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount gsa = task.getResult();
                firebaseAuthWithGoogle(gsa);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount gsa){
        AuthCredential authCredential = GoogleAuthProvider.getCredential(gsa.getIdToken(), null);
        mAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful()){
                            final String currentUID = mAuth.getCurrentUser().getUid();
                            CollectionReference collectionReference = fStore.collection("users");

                            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task)
                                {
                                    if(task.isSuccessful()){
                                        QuerySnapshot querySnapshots = task.getResult();

                                        Toast.makeText(LoginActivity.this, "Before SIZE", Toast.LENGTH_SHORT).show();

                                        if(querySnapshots.size() > 0){
                                            List<User> users = querySnapshots.toObjects(User.class);
                                            for(User user: users){
                                                if(user.getUid().equals(currentUID)){
                                                    Toast.makeText(LoginActivity.this, "We found one equal!", Toast.LENGTH_SHORT).show();

                                                    goToMainActivity();
                                                    isGoogleSign = true;
                                                    return;
                                                }
                                            }
                                            final EditText nameInput = new EditText(LoginActivity.this);
                                            nameInput.setInputType(InputType.TYPE_CLASS_TEXT);

                                            Toast.makeText(LoginActivity.this, "We should be openning a dialog", Toast.LENGTH_SHORT).show();

                                            final Dialog dialog = new Dialog(LoginActivity.this);
                                            dialog.setTitle("Details account");
                                            dialog.setContentView(R.layout.custom_dialog_google_sign_up);
                                            dialog.show();

                                            final EditText editName = dialog.findViewById(R.id.custom_dialog_sign_up_edit_name);
                                            final EditText editAge = dialog.findViewById(R.id.custom_dialog_sign_up_edit_age);
                                            final EditText editLocation = dialog.findViewById(R.id.custom_dialog_sign_up_edit_location);
                                            Button btnOk = dialog.findViewById(R.id.custom_dialog_sign_up_ok_btn);

                                            btnOk.setOnClickListener(new View.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(View v)
                                                {
                                                    String name = editName.getText().toString();
                                                    String age = editAge.getText().toString();
                                                    String location = editLocation.getText().toString();
                                                    String email = gsa.getEmail();
                                                    if(!name.equals("")){
                                                        Map<String, Object> userMap = new HashMap<>();
                                                        userMap.put(KEY_NAME, name);
                                                        userMap.put(KEY_UID, currentUID);
                                                        userMap.put(KEY_EMAIL, gsa.getEmail());
                                                        userMap.put(KEY_AGE, age);
                                                        userMap.put(KEY_PASSWORD, "");
                                                        userMap.put(KEY_STATUS, "");
                                                        userMap.put(KEY_PROFILE, "");
                                                        userMap.put(KEY_LOCATION, location);
                                                        userMap.put(KEY_IS_GOOGLE_SIGN, true);

                                                        DocumentReference docRef = fStore.collection("users").document(currentUID);

                                                        docRef.set(userMap).addOnSuccessListener(new OnSuccessListener<Void>()
                                                        {
                                                            @Override
                                                            public void onSuccess(Void aVoid)
                                                            {
                                                                Toast.makeText(LoginActivity.this, "You're registered!", Toast.LENGTH_SHORT).show();
                                                                goToMainActivity();
                                                                dialog.dismiss();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener()
                                                        {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e)
                                                            {
                                                                Toast.makeText(LoginActivity.this, "Damn you couldn't be registered", Toast.LENGTH_SHORT).show();

                                                            }
                                                        });

                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void forgotPassword(){
        String email = mEtEmail.getText().toString();

        if(!TextUtils.isEmpty(email)){
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "We've sent to your email if there's any in the database", Toast.LENGTH_SHORT).show();
                    }else{
                        String exception = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, exception, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
