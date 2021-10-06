package com.example.nimble;

import android.Manifest;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import yuku.ambilwarna.AmbilWarnaDialog;

public class HomeActivity extends AppCompatActivity {

    // VARIABLES
    TextView name_tv;
    ImageView avatar_iv;

    // DRAWING
    SignatureView signatureView;
    ImageButton btn_color, btn_eraser, btn_save;
    int defaultColor;

    // FOR SAVING DRAWINGS
    private static String fileName;
    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myPaintings");

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

        // FIND DRAWING BUTTONS IN INTERFACE
        signatureView = findViewById(R.id.signature_view);
        btn_color = findViewById(R.id.btn_color);
        btn_eraser = findViewById(R.id.btn_erase);
        btn_save = findViewById(R.id.btn_save);

        // SET RANDOM
        r = new Random();

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String name = sharedPreferences.getString(USER_NAME, null);

        if (name != null) {
            // SET THE NAME TO TEXTVIEW
            name_tv.setText(name);
        }

        // SET DEFAULT COLOR
        defaultColor = ContextCompat.getColor(HomeActivity.this, R.color.black);

        //COLOR PICKER
        btn_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });

        // PICTURE ERASER
        btn_eraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
            }
        });

        // CREATE A TIME STRING TO SAVE IMAGES AS
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date = format.format(new Date());
        fileName = path + "/" + date + ".png";

        // CHECK IF PATH EXISTS
        if (!path.exists()){
          path.mkdirs();
        };

        // SAVE PICTURE
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!signatureView.isBitmapEmpty()){
                    try {
                        saveImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    private void saveImage() throws IOException {
        File file = new File(fileName);

        Bitmap bitmap = signatureView.getSignatureBitmap();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapData = bos.toByteArray();

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();

        Toast.makeText(this, "Painting Saved!", Toast.LENGTH_SHORT).show();

    }

    private void openColorPicker() {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                defaultColor = color;
                signatureView.setPenColor(color);

            }
        });
        ambilWarnaDialog.show();
    }

}