package com.example.finalprojectapplication.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalprojectapplication.Model.User;
import com.example.finalprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements MenuItem.OnMenuItemClickListener
{

    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_AGE = "age";

    private FirebaseFirestore fstore;
    private FirebaseAuth fAuth;

    private String userID;

    private TextView mProfileName, mProfileEmail, mProfilePassword,  mProfileLocation, mProfileAge;
    private EditText mProfileNameEdit, mProfileEmailEdit, mProfilePasswordEdit,  mProfileLocationEdit, mProfileAgeEdit;
    private ImageView mProfilePicture;

    private LinearLayout mContainerInfo, mContainerEditInfo;

    private View view;
    private Toolbar mToolbarEdit;
    Menu menu;

    MenuItem editItem, checkItem, cancelItem;


    public ProfileFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        this.view = view;

        setupFirebase();
        setupViewComponents();
        getUserInformation();
    }


    public void setupViewComponents(){

        mToolbarEdit = view.findViewById(R.id.profile_editToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbarEdit);


        mContainerInfo = view.findViewById(R.id.containerInformation);
        mContainerEditInfo = view.findViewById(R.id.containerEditInformation);


        mProfilePicture = view.findViewById(R.id.profilePicture);
        mProfileName = view.findViewById(R.id.profileName);
        mProfileEmail= view.findViewById(R.id.profileEmail);
        mProfilePassword = view.findViewById(R.id.profilePassword);
        mProfileAge = view.findViewById(R.id.profileAge);
        mProfileLocation = view.findViewById(R.id.profileLocation);

        mProfileNameEdit = view.findViewById(R.id.profileEditName);
        mProfileEmailEdit= view.findViewById(R.id.profileEditEmail);
        mProfilePasswordEdit = view.findViewById(R.id.profileEditPassword);
        mProfileAgeEdit = view.findViewById(R.id.profileEditAge);
        mProfileLocationEdit = view.findViewById(R.id.profileEditLocation);


    }

    private void setupFirebase(){

        fstore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
    }



    private void getUserInformation(){
        DocumentReference userRef = fstore.collection("users").document(userID);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);

                        if(user!=null){
                            if(user.getProfilePictureUrl().equals("")){
                                    mProfilePicture.setImageResource(R.drawable.ic_profile_identity_gray);
                            }else{
                                Picasso.with(getContext()).load(user.getProfilePictureUrl()).into(mProfilePicture);
                            }

                            mProfileName.setText(user.getName());
                            mProfileEmail.setText(user.getEmail());
                            mProfilePassword.setText(user.getPassword());
                            mProfileAge.setText(user.getAge());
                            mProfileLocation.setText(user.getLocation());

                        }

                    }
                }
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.editable_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);

        editItem = menu.findItem(R.id.edit_pen_action);
        checkItem = menu.findItem(R.id.check_edit_action);
        cancelItem = menu.findItem(R.id.cancel_edit_action);

        editItem.setOnMenuItemClickListener(this);
        cancelItem.setOnMenuItemClickListener(this);
        checkItem.setOnMenuItemClickListener(this);

    }


    @Override
    public boolean onMenuItemClick(MenuItem item)
    {
        switch(item.getItemId()){
            case R.id.edit_pen_action:
                activeEditMode();
                break;
            case R.id.check_edit_action:
                updateData();
                break;
            case R.id.cancel_edit_action:
                containerInTextMode();
                break;
        }
        return true;
    }

    public void activeEditMode(){
        editItem.setVisible(false);
        cancelItem.setVisible(true);
        checkItem.setVisible(true);


        containerInEditMode();
        mContainerEditInfo.setVisibility(View.VISIBLE);
        mContainerInfo.setVisibility(View.INVISIBLE);


    }

    public void containerInEditMode(){
        mProfileNameEdit.setText(mProfileName.getText());
        mProfileEmailEdit.setText(mProfileEmail.getText());
        mProfilePasswordEdit.setText(mProfilePassword.getText());
        mProfileAgeEdit.setText(mProfileAge.getText());
        mProfileLocationEdit.setText(mProfileLocation.getText());

    }

    public void containerInTextMode(){
        editItem.setVisible(true);
        cancelItem.setVisible(false);
        checkItem.setVisible(false);

        mContainerEditInfo.setVisibility(View.INVISIBLE);
        mContainerInfo.setVisibility(View.VISIBLE);
    }

    public void updateData(){
        updateDataInCloud();
    }

    public void updateDataInCloud(){
        final String name = mProfileNameEdit.getText().toString();
        final String email = mProfileEmailEdit.getText().toString();
        final String password = mProfilePasswordEdit.getText().toString();
        final String age = mProfileAgeEdit.getText().toString();
        final String location = mProfileLocationEdit.getText().toString();

        if(name.equals("") || email.equals("") || password.equals("") || age.equals("") || location.equals("")){
           Toast.makeText(getContext(), "Fields can't be empty", Toast.LENGTH_SHORT).show();
        }else if(password.length() < 6){
            Toast.makeText(getContext(), "Password has to 6 or more characters", Toast.LENGTH_SHORT).show();
        }else{
            Map<String, Object> user = new HashMap<>();
            user.put(KEY_NAME, name);
            user.put(KEY_EMAIL, email);
            user.put(KEY_PASSWORD, password);
            user.put(KEY_AGE, age);
            user.put(KEY_LOCATION, location);

            DocumentReference userRef = fstore.collection("users").document(userID);
            userRef.update(user).addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    Toast.makeText(getContext(), "Data updated", Toast.LENGTH_SHORT).show();
                    setDataToTextLocal(name, email, password, age, location);

                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(getContext(), "Data couldn't be updated, try later", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void setDataToTextLocal(String name, String email, String password, String age, String location){
        mProfileName.setText(name);
        mProfileEmail.setText(email);
        mProfilePassword.setText(password);
        mProfileAge.setText(age);
        mProfileLocation.setText(location);

        containerInTextMode();
    }

}
