package com.example.nimble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // VARIABLES
    EditText name_et;
    Button start_btn;
    SharedPreferences sharedPreferences;

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String USER_NAME = "userName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // FIND EDITTEXT BOX AND BUTTON FROM INTERFACE
        name_et = findViewById(R.id.name_et);
        start_btn = findViewById(R.id.start_btn);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        // ADD TO SHARED PREFERENCES WHEN BUTTON CLICKED AND CHANGE PAGE
        start_btn.setOnClickListener(v -> {

            // ADD NAME TO SHARED PREFS
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(USER_NAME, name_et.getText().toString());
            editor.apply();

            // NAVIGATE TO NEXT PAGE
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);

            // MAKE TOAST TO SAY IF SUCCESSFUL
            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
        });
    }
}