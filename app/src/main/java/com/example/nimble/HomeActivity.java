package com.example.nimble;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
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
    TextView name_tv, image_tv, translated_tv;
    ImageButton avatar_iv;
    Button btn_submit, btn_popup_close, btn_popup_next, btn_popup_okay;
    
    public String translateText = "";
    public String test = "";
    //public String translatedText = "";

    // DRAWING
    SignatureView signatureView;
    ImageButton btn_color, btn_eraser, btn_save;
    int defaultColor;

    // FOR SAVING DRAWINGS
    private static String fileName;
    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/myPaintings");

    // POPUP BOX
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog, dialog2;

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

    // CREATE ENGLISH - SPANISH TRANSLATOR
    TranslatorOptions options =
            new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.SPANISH)
                    .build();
    final Translator englishSpanishTranslator =
            Translation.getClient(options);


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
        image_tv = findViewById(R.id.image_tv);


        // FIND IMAGE IN INTERFACE
        avatar_iv = findViewById(R.id.avatar_iv);

        // FIND SUBMIT BUTTON IN INTERFACE
        btn_submit = findViewById(R.id.btn_submit);

        // PROFILE BUTTON
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
            public void onClick(View view) {
                if (!signatureView.isBitmapEmpty()){
                    try {
                        saveImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(HomeActivity.this, "Couldn't save!", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });

        // SUBMIT PICTURE
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!signatureView.isBitmapEmpty()){
                    createDiaglog();

                }
                else {
                    Toast.makeText(HomeActivity.this, "Draw something first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // PROFILE BUTTON
        avatar_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // NAVIGATE TO NEXT PAGE
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    // ON SUBMIT - CHECK WHAT WAS DRAWN POPUP
    public void createDiaglog(){

        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView = getLayoutInflater().inflate(R.layout.popup_first, null);

        btn_popup_next = popupView.findViewById(R.id.btn_popup_next);
        btn_popup_close = popupView.findViewById(R.id.btn_popup_close);
        image_tv = popupView.findViewById(R.id.image_tv);

        Bitmap bitmap = signatureView.getSignatureBitmap();

        InputImage image = InputImage.fromBitmap(bitmap, 0);

        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        // Task completed successfully
                        String words = "";

                        for (ImageLabel label : labels) {

                            String firstLabel = labels.get(0).getText();

                            String text = label.getText();
                            float confidence = label.getConfidence();
                            int index = label.getIndex();

                            translateText = firstLabel;

                        }
                        image_tv.setText(translateText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });

        dialogBuilder.setView(popupView);
        dialog = dialogBuilder.create();
        dialog.show();

        //  OPEN TRANSLATION POPUP
        btn_popup_next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
                dialog.dismiss();
                downloadTranslator();
                createDiaglog2();
            }
        });

        // CLOSE THE POPUP
        btn_popup_close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
                dialog.dismiss();
            }
        });

    }

    private void translate() {



    }

    private void downloadTranslator() {

        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        englishSpanishTranslator.downloadModelIfNeeded(conditions);

    }

    // IF DRAWING CORRECT - OPEN TRANSLATION POPUP
    public void createDiaglog2(){

        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView2 = getLayoutInflater().inflate(R.layout.popup_second, null);

        btn_popup_next = popupView2.findViewById(R.id.btn_popup_okay);
        translated_tv = popupView2.findViewById(R.id.translated_tv);

        dialogBuilder.setView(popupView2);
        dialog2 = dialogBuilder.create();

        // TRANSLATE THE TEXT
        englishSpanishTranslator.translate(translateText)
                .addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(@NonNull String translatedText) {
                        Toast.makeText(HomeActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        translated_tv.setText(translatedText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, "Could not translate", Toast.LENGTH_SHORT).show();
                    }
                });

        dialog2.show();

        // CLOSE THE POPUP
        btn_popup_close.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
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
