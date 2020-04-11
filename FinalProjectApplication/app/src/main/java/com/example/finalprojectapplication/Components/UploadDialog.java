package com.example.finalprojectapplication.Components;





import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.finalprojectapplication.R;


//Dialog Abandoned
public class UploadDialog
{
    Activity activity;

    public UploadDialog(final Activity activity){
        this.activity = activity;

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog_upload);
        dialog.setTitle("Upload your file");

        Button uploadButton = dialog.findViewById(R.id.uploadButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        ImageView imageUpload = dialog.findViewById(R.id.imageToUpload);

        imageUpload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("Image working");

                Intent takePicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activity.startActivityForResult(takePicture, 0);



            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("upload working");
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                System.out.println("Cnacel working");
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
