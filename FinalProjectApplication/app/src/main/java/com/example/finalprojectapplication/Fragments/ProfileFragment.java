package com.example.finalprojectapplication.Fragments;


import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.finalprojectapplication.Model.User;
import com.example.finalprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment
{

    private FirebaseFirestore fstore;
    private FirebaseAuth fAuth;

    private String userID;

    private TextView mProfileName, mProfileEmail, mProfilePassword,  mProfileLocation, mProfileAge;
    private EditText mProfileNameEdit, mProfileEmailEdit, mProfilePasswordEdit,  mProfileLocationEdit, mProfileAgeEdit;
    private ImageView mProfilePicture;

    private LinearLayout mContainerInfo, mContainerEditInfo;

    private View view;


    public ProfileFragment()
    {
        // Required empty public constructor
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
        setupListeners();


    }

    public void setupViewComponents(){

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


    private void setupListeners(){
        mProfileName.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mContainerInfo.setVisibility(View.INVISIBLE);
                mContainerEditInfo.setVisibility(View.VISIBLE);

                mProfileNameEdit.setText(mProfileName.getText().toString());
                mProfileEmail.setText(mProfileEmail.getText().toString());
                mProfilePasswordEdit.setText(mProfilePassword.getText().toString());
                mProfileAgeEdit.setText(mProfileAge.getText().toString());
                mProfileLocationEdit.setText(mProfileLocation.getText().toString());

            }
        });


    }

}
