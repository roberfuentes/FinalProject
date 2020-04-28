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
public class InfoDialog
{
    Activity activity;

    public InfoDialog(final Activity activity){
        this.activity = activity;

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog_google_sign_up);


        dialog.show();
    }

}
