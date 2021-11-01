package com.example.nimble;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends AppCompatActivity {

    // VARIABLES
    TextView username_tv;
    Button btn_draw, btn_edit, btn_save;
    EditText name_et;

    // CALL SHARED PREFS
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String USER_NAME = "userName";
    private static final String USER_IMAGE = "userImage";

    // POPUP BOX
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog editDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // FIND TEXTVIEW FOR USERNAME
        username_tv = findViewById(R.id.username_tv);

        // FIND BUTTONS
        btn_draw = findViewById(R.id.btn_draw);
        btn_edit = findViewById(R.id.btn_edit);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String name = sharedPreferences.getString(USER_NAME, null);
        String image = sharedPreferences.getString(USER_IMAGE, null);

        if (name != null) {
            // SET THE NAME TO TEXTVIEW
            username_tv.setText(name);
        }

        // ON BUTTON CLICK
        btn_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NAVIGATE TO NEXT PAGE
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // ON IMAGE CLICK
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            createDialog();

            }
        });

    }

    // FUNCTION TO CREATE POPUP
    private void createDialog() {

        // BUILD DIALOG
        dialogBuilder = new AlertDialog.Builder(this);
        final View editPopup = getLayoutInflater().inflate(R.layout.popup_edit, null);
        dialogBuilder.setView(editPopup);
        editDialog = dialogBuilder.create();
        editDialog.show();

        // FIND SAVE BUTTON
        btn_save = editPopup.findViewById(R.id.save_btn);
        // FIND NAME TEXT
        name_et = editPopup.findViewById(R.id.name_et);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (name_et.getText().toString() == "") {

                    // MAKE TOAST TO TELL USER TO ENTER A NAME
                    Toast.makeText(ProfileActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                }
                else {

                    // ADD NAME TO SHARED PREFS
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(USER_NAME, name_et.getText().toString());
                    editor.apply();

                    // CLOSE EDIT POPUP
                    editDialog.dismiss();
                }

            }
        });

    }
}