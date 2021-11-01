package com.example.nimble;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.cloud.sdk.core.security.IamToken;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

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

    // RANDOM
    final Random myRandom = new Random();

    // SET ARRAY OF IMAGES
    Integer [] images = {
      R.drawable.avatar_1,
      R.drawable.avatar_2,
      R.drawable.avatar_3,
      R.drawable.avatar_4,
      R.drawable.avatar_5,
      R.drawable.avatar_6
    };

    // SETUP TRANSLATOR
    Translator myTranslator;
    TranslatorOptions options;

    // CALL SHARED PREFS
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String USER_NAME = "userName";
    private static final String USER_IMAGE = "userImage";
    private static final String USER_LANGUAGE = "userLanguage";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String name = sharedPreferences.getString(USER_NAME, null);
        String image = sharedPreferences.getString(USER_IMAGE, null);

        // !  NOT WORKING
        // LANGUAGE PICKER
        String language = sharedPreferences.getString(USER_LANGUAGE, null);

//        if (language.equals("AFRIKAANS")){
//            options = new TranslatorOptions.Builder()
//                    .setSourceLanguage(TranslateLanguage.ENGLISH)
//                    .setTargetLanguage(TranslateLanguage.AFRIKAANS)
//                    .build();
//
//            myTranslator = Translation.getClient(options);
//        }
//        else if (language.equals("SPANISH")){
//            options = new TranslatorOptions.Builder()
//                    .setSourceLanguage(TranslateLanguage.ENGLISH)
//                    .setTargetLanguage(TranslateLanguage.SPANISH)
//                    .build();
//
//            myTranslator = Translation.getClient(options);
//        }
//        else if (language.equals("FRENCH")){
//            options = new TranslatorOptions.Builder()
//                    .setSourceLanguage(TranslateLanguage.ENGLISH)
//                    .setTargetLanguage(TranslateLanguage.FRENCH)
//                    .build();
//
//            myTranslator = Translation.getClient(options);
//        }
//        else if (language.equals("GERMAN")){
//            options = new TranslatorOptions.Builder()
//                    .setSourceLanguage(TranslateLanguage.ENGLISH)
//                    .setTargetLanguage(TranslateLanguage.GERMAN)
//                    .build();
//
//            myTranslator = Translation.getClient(options);
//        }

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

        if (name != null) {
            // SET THE NAME TO TEXTVIEW
            name_tv.setText(name);
        }

        // SET RANDOM AVATAR
        avatar_iv.setImageResource(images[myRandom.nextInt(images.length)]);

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
        }

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

                        for (ImageLabel label : labels) {

                            // GET THE FIRST LABEL
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

    private void downloadTranslator() {

    }

    // IF DRAWING CORRECT - OPEN TRANSLATION POPUP
    public void createDiaglog2(){

        dialogBuilder = new AlertDialog.Builder(this);
        final View popupView2 = getLayoutInflater().inflate(R.layout.popup_second, null);

        translated_tv = popupView2.findViewById(R.id.translated_tv);
        btn_popup_okay = popupView2.findViewById(R.id.btn_popup_okay);

        dialogBuilder.setView(popupView2);
        dialog2 = dialogBuilder.create();


        // LANGUAGE PICKER
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String language = sharedPreferences.getString(USER_LANGUAGE, null);
        RemoteModelManager modelManager = RemoteModelManager.getInstance();

        modelManager.getDownloadedModels(TranslateRemoteModel.class)
                .addOnSuccessListener(new OnSuccessListener<Set<TranslateRemoteModel>>() {
                    @Override
                    public void onSuccess(@NonNull Set<TranslateRemoteModel> translateRemoteModels) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        if (language.equals("AFRIKAANS")){
                    Toast.makeText(HomeActivity.this, "Afrikaans", Toast.LENGTH_SHORT).show();

            TranslateRemoteModel afrikaansModel =
                    new TranslateRemoteModel.Builder(TranslateLanguage.AFRIKAANS).build();

            DownloadConditions conditions = new DownloadConditions.Builder()
                    .requireWifi()
                    .build();

            modelManager.download(afrikaansModel, conditions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void unused) {
                            // MODEL DOWNLOADED
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // ERROR
                        }
                    });

            options = new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.AFRIKAANS)
                    .build();

            myTranslator = Translation.getClient(options);


        }
        else if (language.equals("SPANISH")){
                    Toast.makeText(HomeActivity.this, "Spanish", Toast.LENGTH_SHORT).show();

            TranslateRemoteModel spanishModel =
                    new TranslateRemoteModel.Builder(TranslateLanguage.SPANISH).build();

            DownloadConditions conditions = new DownloadConditions.Builder()
                    .requireWifi()
                    .build();

            modelManager.download(spanishModel, conditions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void unused) {
                            // MODEL DOWNLOADED
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // ERROR
                        }
                    });

            options = new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.SPANISH)
                    .build();

            myTranslator = Translation.getClient(options);

        }
        else if (language.equals("FRENCH")){
                    Toast.makeText(HomeActivity.this, "French", Toast.LENGTH_SHORT).show();

                    TranslateRemoteModel frenchModel =
                            new TranslateRemoteModel.Builder(TranslateLanguage.FRENCH).build();

                    DownloadConditions conditions = new DownloadConditions.Builder()
                            .requireWifi()
                            .build();

                    modelManager.download(frenchModel, conditions)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(@NonNull Void unused) {
                                    // MODEL DOWNLOADED
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // ERROR
                                }
                            });

            options = new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.FRENCH)
                    .build();

            myTranslator = Translation.getClient(options);

        }
        else if (language.equals("GERMAN")){
                    Toast.makeText(HomeActivity.this, "German", Toast.LENGTH_SHORT).show();

            TranslateRemoteModel germanModel =
                    new TranslateRemoteModel.Builder(TranslateLanguage.GERMAN).build();

            DownloadConditions conditions = new DownloadConditions.Builder()
                    .requireWifi()
                    .build();

            modelManager.download(germanModel, conditions)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull Void unused) {
                            // MODEL DOWNLOADED
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // ERROR
                        }
                    });

            options = new TranslatorOptions.Builder()
                    .setSourceLanguage(TranslateLanguage.ENGLISH)
                    .setTargetLanguage(TranslateLanguage.GERMAN)
                    .build();

            myTranslator = Translation.getClient(options);
        }


//        // DOWNLOAD THE TRANSLATOR IF NEEDED
//        myTranslator.downloadModelIfNeeded()
//                .addOnSuccessListener(
//                        new OnSuccessListener() {
//                            @Override
//                            public void onSuccess(Object o) {
//                                // Model downloaded successfully. Okay to start translating.
//                                // (Set a flag, unhide the translation UI, etc.)
//                            }
//                        })
//                .addOnFailureListener(
//                        new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                // Model couldn’t be downloaded or other internal error.
//                                // ...
//                                Toast.makeText(HomeActivity.this, "Could not download", Toast.LENGTH_SHORT).show();
//
//                            }
//                        });


        // TRANSLATE THE TEXT
        myTranslator.translate(translateText)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText) {
                                // Translation successful.
                                Toast.makeText(HomeActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                translated_tv.setText(translatedText);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error.
                                Toast.makeText(HomeActivity.this, "Could not translate", Toast.LENGTH_SHORT).show();
                            }
                        });


        dialog2.show();

        // CLOSE THE POPUP
        btn_popup_okay.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                myTranslator.close();
            }
        });

    }

    // SAVE THE IMAGE DRAWN
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

    // OPEN COLOUR PICKER
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
