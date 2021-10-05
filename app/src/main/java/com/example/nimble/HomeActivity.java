package com.example.nimble;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    // VARIABLES
    TextView name_tv;
    ImageView avatar_iv;

    Random r;

    // SET ARRAY OF IMAGES
    Integer [] images = {
      R.drawable.avatar_1,
      R.drawable.avatar_2,
      R.drawable.avatar_3,
      R.drawable.avatar_4,
      R.drawable.avatar_5,
      R.drawable.avatar_6
    };

    // CALL SHARED PREFS
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String USER_NAME = "userName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // TODO: FIX AVATAR DISPLAY
        // DISPLAY RANDOM AVATAR
//        avatar_iv.setImageResource(images[r.nextInt(images.length)]);

        // FIND TEXTVIEW IN INTERFACE
        name_tv = findViewById(R.id.name_tv);

        // FIND IMAGE IN INTERFACE
        avatar_iv = findViewById(R.id.avatar_iv);

        // SET RANDOM
        r = new Random();


        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String name = sharedPreferences.getString(USER_NAME, null);

        if (name != null) {
            // SET THE NAME TO TEXTVIEW
            name_tv.setText(name);
        }

    }
}
