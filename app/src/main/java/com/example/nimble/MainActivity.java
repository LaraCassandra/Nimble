package com.example.nimble;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // VARIABLES
    EditText name_et;
    Button start_btn;
    Spinner spinner;

    String language = "";

    // CALL SHARED PREF
    SharedPreferences sharedPreferences;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String USER_NAME = "userName";
    private static final String USER_LANGUAGE = "userLanguage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FUNCTION ASK PERMISSION TO SAVE IMAGE
        askPermission();

        // FIND EDITTEXT BOX AND BUTTON FROM INTERFACE
        name_et = findViewById(R.id.name_et);
        start_btn = findViewById(R.id.start_btn);

        // LANGUAGES PICKER
        spinner = findViewById(R.id.languages);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.languages, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        // CHECK IF SHARED PREF DATA AVAILABLE OR NOT
        String name = sharedPreferences.getString(USER_NAME, null);

        if (name != null){
            // IF DATA IS AVAILABLE, GO DIRECTLY TO HOME PAGE
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        }

        // ADD TO SHARED PREFERENCES WHEN BUTTON CLICKED AND CHANGE PAGE
        start_btn.setOnClickListener(v -> {

            if (name_et.getText().toString() == "") {

                // MAKE TOAST TO TELL USER TO ENTER A NAME
                Toast.makeText(MainActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
            }
            else {

                // ADD NAME TO SHARED PREFS
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(USER_NAME, name_et.getText().toString());
                editor.apply();

                // NAVIGATE TO NEXT PAGE
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);

                // MAKE TOAST TO SAY IF SUCCESSFUL
                Toast.makeText(MainActivity.this, "Successful", Toast.LENGTH_SHORT).show();
            }

        });
    }

    //
    private void askPermission(){
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        Toast.makeText(MainActivity.this, "Granted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        language = parent.getItemAtPosition(position).toString().toUpperCase();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USER_LANGUAGE, language);
            editor.apply();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }
}