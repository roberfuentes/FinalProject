package com.example.finalprojectapplication.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

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

import com.example.finalprojectapplication.Activities.Login.LoginActivity;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements MenuItem.OnMenuItemClickListener, View.OnClickListener
{

    private final String KEY_NAME = "name";
    private final String KEY_EMAIL = "email";
    private final String KEY_PASSWORD = "password";
    private final String KEY_AGE = "age";
    private final String KEY_STATUS = "status";
    private final String KEY_LOCATION = "location";
    private final String KEY_IS_GOOGLE_SIGN = "isGoogleSign";


    private final int REQUEST_IMAGE = 1;


    private FirebaseFirestore fstore;
    private FirebaseAuth fAuth;

    private String userID;
    private Boolean isGoogleSign;

    private TextView mProfileName, mProfileEmail, mProfilePassword, mProfileLocation, mProfileAge, mProfileStatus;
    private EditText mProfileNameEdit, mProfileEmailEdit, mProfilePasswordEdit, mProfileLocationEdit, mProfileAgeEdit, mProfileStatusEdit;
    private CircleImageView mProfilePicture;

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
        setupListeners();
        getUserInformation();

    }

    public void setupViewComponents()
    {
        /*mToolbarEdit = view.findViewById(R.id.profile_editToolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbarEdit);*/

        mContainerInfo = view.findViewById(R.id.containerInformation);
        mContainerEditInfo = view.findViewById(R.id.containerEditInformation);

        mProfilePicture = view.findViewById(R.id.profilePicture);
        mProfileName = view.findViewById(R.id.profileName);
        mProfileEmail = view.findViewById(R.id.profileEmail);
        mProfilePassword = view.findViewById(R.id.profilePassword);
        mProfileAge = view.findViewById(R.id.profileAge);
        mProfileStatus = view.findViewById(R.id.profileStatus);
        mProfileLocation = view.findViewById(R.id.profileLocation);

        mProfileNameEdit = view.findViewById(R.id.profileEditName);
        mProfileEmailEdit = view.findViewById(R.id.profileEditEmail);
        mProfilePasswordEdit = view.findViewById(R.id.profileEditPassword);
        mProfileAgeEdit = view.findViewById(R.id.profileEditAge);
        mProfileStatusEdit = view.findViewById(R.id.profileEditStatus);
        mProfileLocationEdit = view.findViewById(R.id.profileEditLocation);
    }

    private void setupListeners()
    {
        mProfilePicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int viewID = v.getId();
        Toast.makeText(getContext(), "ID:" + viewID, Toast.LENGTH_SHORT).show();
        switch (viewID)
        {
            case R.id.profilePicture:
                openImage();
                break;
        }
    }

    private void openImage()
    {
        Toast.makeText(getContext(), "Try to open", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        getActivity().startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void setupFirebase()
    {
        fstore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
    }

    private void getUserInformation()
    {
        DocumentReference userRef = fstore.collection("users").document(userID);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task)
            {
                if (task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists())
                    {
                        User user = documentSnapshot.toObject(User.class);

                        System.out.println(user.getUid() + " and " + user.getIsGoogleSign());

                        if (user != null)
                        {
                            if (user.getIsGoogleSign())
                            {
                                mProfileEmailEdit.setEnabled(false);
                                mProfilePasswordEdit.setEnabled(false);
                            }

                            if (user.getProfilePictureUrl().equals(""))
                            {
                                mProfilePicture.setImageResource(R.drawable.ic_profile_identity_gray);
                            } else
                            {
                                Picasso.with(getContext()).load(user.getProfilePictureUrl()).into(mProfilePicture);
                            }
                            mProfileName.setText(user.getName());
                            mProfileEmail.setText(user.getEmail());
                            mProfileAge.setText(user.getAge());
                            mProfileStatus.setText(user.getStatus());
                            mProfileLocation.setText(user.getLocation());

                            if (!user.getPassword().equals(LoginActivity.password))
                            {
                                updatePassword(user);
                            } else
                            {
                                mProfilePassword.setText(user.getPassword());
                            }
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
        switch (item.getItemId())
        {
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

    public void activeEditMode()
    {
        editItem.setVisible(false);
        cancelItem.setVisible(true);
        checkItem.setVisible(true);


        containerInEditMode();
        mContainerEditInfo.setVisibility(View.VISIBLE);
        mContainerInfo.setVisibility(View.INVISIBLE);
    }

    public void containerInEditMode()
    {
        mProfileNameEdit.setText(mProfileName.getText());
        mProfileEmailEdit.setText(mProfileEmail.getText());
        mProfilePasswordEdit.setText(mProfilePassword.getText());
        mProfileAgeEdit.setText(mProfileAge.getText());
        mProfileStatusEdit.setText(mProfileStatus.getText());
        mProfileLocationEdit.setText(mProfileLocation.getText());
    }

    public void containerInTextMode()
    {
        editItem.setVisible(true);
        cancelItem.setVisible(false);
        checkItem.setVisible(false);

        mContainerEditInfo.setVisibility(View.INVISIBLE);
        mContainerInfo.setVisibility(View.VISIBLE);
    }

    public void updateData()
    {
        updateDataInCloud();
    }

    public void updateDataInCloud()
    {
        final String name = mProfileNameEdit.getText().toString();
        final String email = mProfileEmailEdit.getText().toString();
        final String password = mProfilePasswordEdit.getText().toString();
        final String age = mProfileAgeEdit.getText().toString();
        final String location = mProfileLocationEdit.getText().toString();
        final String status = mProfileStatusEdit.getText().toString();


        if (name.equals("") || email.equals("") || password.equals("") || age.equals("") || location.equals(""))
        {
            Toast.makeText(getContext(), "Fields can't be empty", Toast.LENGTH_SHORT).show();
        } else if (password.length() < 6)
        {
            Toast.makeText(getContext(), "Password has to 6 or more characters", Toast.LENGTH_SHORT).show();
        } else
        {
            Map<String, Object> user = new HashMap<>();
            user.put(KEY_NAME, name);
            user.put(KEY_EMAIL, email);
            user.put(KEY_PASSWORD, password);
            user.put(KEY_AGE, age);
            user.put(KEY_LOCATION, location);
            user.put(KEY_STATUS, status);
            user.put(KEY_IS_GOOGLE_SIGN, isGoogleSign);


            DocumentReference userRef = fstore.collection("users").document(userID);


            userRef.update(user).addOnSuccessListener(new OnSuccessListener<Void>()
            {
                @Override
                public void onSuccess(Void aVoid)
                {
                    Toast.makeText(getContext(), "Data updated", Toast.LENGTH_SHORT).show();
                    setDataToTextLocal(name, email, password, age, status, location);

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

    private void updatePassword(User user)
    {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(KEY_NAME, user.getName());
        userMap.put(KEY_EMAIL, user.getEmail());
        userMap.put(KEY_PASSWORD, LoginActivity.password);
        userMap.put(KEY_AGE, user.getAge());
        userMap.put(KEY_LOCATION, user.getLocation());
        userMap.put(KEY_STATUS, user.getStatus());
        userMap.put(KEY_IS_GOOGLE_SIGN, user.getIsGoogleSign());

        DocumentReference userRef = fstore.collection("users").document(userID);

        userRef.update(userMap).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    mProfilePassword.setText(LoginActivity.password);
                } else
                {
                    Toast.makeText(getActivity(), "There was an error", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }


    public void setDataToTextLocal(String name, String email, String password, String age, String status, String location)
    {
        mProfileName.setText(name);
        mProfileEmail.setText(email);
        mProfilePassword.setText(password);
        mProfileAge.setText(age);
        mProfileStatus.setText(status);
        mProfileLocation.setText(location);

        containerInTextMode();
    }
}
