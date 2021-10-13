package com.example.nimble;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    // VARIABLES
    TextView username_tv;
    Button btn_draw;

    // CALL SHARED PREFS
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String USER_NAME = "userName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // FIND TEXTVIEW FOR USERNAME
        username_tv = findViewById(R.id.username_tv);

        btn_draw = findViewById(R.id.btn_draw);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String name = sharedPreferences.getString(USER_NAME, null);

        if (name != null) {
            // SET THE NAME TO TEXTVIEW
            username_tv.setText(name);
        }

        btn_draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NAVIGATE TO NEXT PAGE
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }
}