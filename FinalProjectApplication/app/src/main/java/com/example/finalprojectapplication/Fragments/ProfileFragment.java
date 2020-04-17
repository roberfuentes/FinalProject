package com.example.finalprojectapplication.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalprojectapplication.Model.User;
import com.example.finalprojectapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment
{

    FirebaseFirestore fstore;
    FirebaseAuth fAuth;

    String userID;

    TextView mProfileName, mProfileEmail, mProfilePassword,  mProfileLocation, mProfileAge;
    ImageView mProfilePicture;

    View view;

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


    }

    public void setupViewComponents(){

        mProfilePicture = view.findViewById(R.id.profilePicture);
        mProfileName = view.findViewById(R.id.profileName);
        mProfileEmail= view.findViewById(R.id.profileEmail);
        mProfilePassword = view.findViewById(R.id.profilePassword);
        mProfileAge = view.findViewById(R.id.profileAge);
        mProfileLocation = view.findViewById(R.id.profileLocation);


    }

    public void setupFirebase(){

        fstore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
    }

    public void getUserInformation(){
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

                            mProfileName.setText("Name: "+user.getName());
                            mProfileEmail.setText("Email: "+user.getEmail());
                            mProfilePassword.setText("Password: "+user.getPassword());
                            mProfileAge.setText("Age: "+user.getAge());
                            mProfileLocation.setText("Location: "+user.getLocation());

                        }

                    }
                }
            }
        });
    }

}
