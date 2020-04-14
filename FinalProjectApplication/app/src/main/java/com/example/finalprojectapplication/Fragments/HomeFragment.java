package com.example.finalprojectapplication.Fragments;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.finalprojectapplication.Activities.Main.UploadActivity;
import com.example.finalprojectapplication.Adapters.DataAdapter;
import com.example.finalprojectapplication.Model.Data;
import com.example.finalprojectapplication.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements DataAdapter.OnFileListener
{


    private static final String TAG = "HomeFragment";
    private static final int REQUEST_STORAGE_WRITE_READ = 1;


    //Firebase Cloud
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    private DataAdapter adapter;
    FirebaseStorage mStorageRef;



    private RecyclerView recyclerView;
    private View view;

    FloatingActionButton uploadButton;

    private CharSequence[] chooseOption = new CharSequence[]{
            "Download",
            "Details",
            "Delete",
    };

    public HomeFragment()
    {
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        this.view = view;

        //setup view and data
        setupRecyclerView();
        setupFirebase();
        setupData();

        //setupDataInRecycerView();

        //setup Components and listeners
        setComponents();
        setListeners();

    }


    //SETUP

    public void setComponents()
    {
        uploadButton = view.findViewById(R.id.uploadButton);


    }

    public void setListeners()
    {
        uploadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                openUploadDialog();

            }
        });
    }


    public void setupRecyclerView()
    {
        //Recycler
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    private void setupFirebase()
    {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();


    }


    private void setupData(){
        Query query = fStore.collection("data").document(userID).collection("userData");

        FirestoreRecyclerOptions<Data> options = new FirestoreRecyclerOptions.Builder<Data>().setQuery(query, Data.class).build();

        //FirestoreRecyclerOptions<Image> options = new FirestoreRecyclerOptions.Builder<Image>().setLifecycleOwner(this).setQuery(query,Image.class ).build();

        adapter = new DataAdapter(options, getContext(), this, fStore, fAuth);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));




        recyclerView.setAdapter(adapter);
    }
    //LifeCycle

    @Override
    public void onStart()
    {
        super.onStart();
        System.out.println("Started");
        adapter.startListening();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        System.out.println("Stopped");
        adapter.stopListening();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        adapter.startListening();
    }


    @Override
    public void onFileClick(int position)
    {
        Log.d("ITEM CLICK", "Clicked item:" + position);

        //adapter.deleteItem(position);
        adapter.getInfoItem(position);
    }

    @Override
    public void onFileLongClickListener(final int position)
    {
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setSingleChoiceItems(chooseOption, 0, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which){
                    case 0:
                        dialog.dismiss();
                        adapter.downloadUrl(position);
                        Toast.makeText(getContext(), "This should downnload", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        adapter.getInfoItem(position);
                        dialog.dismiss();
                        Toast.makeText(getContext(), "This should details", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        adapter.deleteItem(position);
                        dialog.dismiss();
                        Toast.makeText(getContext(), "This should delete", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        builder.show();
    }

    //Permissionsç
    private void openUploadDialog()
    {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (ContextCompat.checkSelfPermission(getContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED)
        {


            Intent intent = new Intent(getActivity(), UploadActivity.class);
            startActivity(intent);
            //new UploadDialog(getActivity());  Dialog Abandoned

        } else
        {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_STORAGE_WRITE_READ);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if (requestCode == REQUEST_STORAGE_WRITE_READ)
        {
            openUploadDialog();
        }
    }


}
