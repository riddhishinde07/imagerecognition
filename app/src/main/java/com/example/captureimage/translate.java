package com.example.captureimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class translate extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    TextView text;
    EditText dpart,number;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    String userID;
    Button changelanguage;
    StorageReference storageReference;
    ImageView profileimg;
    FirebaseUser user;
    SharedPreferences sharedPreferences,sharedPreferences1;
    boolean getLoginStatus,isGetLoginStatus;

    FirebaseTranslator firebaseTranslator;
    Spinner spinner;
    ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        spinner = findViewById(R.id.spinner);
        text = findViewById(R.id.text);
        drawerLayout = findViewById(R.id.drawer_layout);
        profileimg = findViewById(R.id.profileimg);

        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nv);
        navigationView.setNavigationItemSelectedListener(this);
        sharedPreferences = getSharedPreferences("googleLogin", Context.MODE_PRIVATE);
        getLoginStatus = sharedPreferences.getBoolean("googleLogin", false);
    //    if(getLoginStatus){
      //      navigationView.getMenu().removeItem(R.id.changepassword);
        //}

        sharedPreferences1 = getSharedPreferences("facebookLogin", Context.MODE_PRIVATE);
        getLoginStatus = sharedPreferences1.getBoolean("facebookLogin", false);
        if (getLoginStatus || isGetLoginStatus){
            navigationView.getMenu().removeItem(R.id.changepassword);
        }


        user = auth.getCurrentUser();
        dpart = findViewById(R.id.dpart);
        number = findViewById(R.id.number);
        ActivityCompat.requestPermissions(translate.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);

        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        translate.this);
                builder.setTitle("Send");
                builder.setMessage("Send Data");

                builder.setNeutralButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Toast.makeText(getApplicationContext(), "Cancel is clicked", Toast.LENGTH_LONG).show();
                            }
                        });
                builder.setNegativeButton("WhatsApp",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String text = dpart.getText().toString();
                                String toNumber = number.getText().toString();


                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="  + "&text=" + text));
                                startActivity(intent);

                            }
                        });
                builder.setPositiveButton("Message",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String text1 = dpart.getText().toString();
                                String phone_Num = number.getText().toString();

                                try {
                                    SmsManager sms = SmsManager.getDefault(); // using android SmsManager
                                    sms.sendTextMessage(phone_Num, null, text1, null, null);
                                    Toast.makeText(translate.this, "Sms Send", Toast.LENGTH_SHORT).show();// adding number and text
                                } catch (Exception e) {
                                    Toast.makeText(translate.this, "Sms not Send", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        });


                builder.show();
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectLang = parent.getItemAtPosition(position).toString();
                switch(selectLang){
                    case "Marathi":
// assigning div item list defined in XML to the div Spinner
                        FirebaseTranslatorOptions options =
                                new FirebaseTranslatorOptions.Builder()
// below line we are specifying our source language.
                                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
// in below line we are displaying our target language.
                                        .setTargetLanguage(FirebaseTranslateLanguage.MR)
// after that we are building our options.
                                        .build();
                        firebaseTranslator= FirebaseNaturalLanguage.getInstance().getTranslator(options);
                        String string = dpart.getText().toString();
                        downloadModal(string);
                        break;

                    case "Hindi":
                        FirebaseTranslatorOptions options1 =
                                new FirebaseTranslatorOptions.Builder()
                                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                        .setTargetLanguage(FirebaseTranslateLanguage.HI)
                                        .build();
                        firebaseTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options1);
                        String string1 = dpart.getText().toString();
                        downloadModal(string1);
                        break;
                    case "German":
                        FirebaseTranslatorOptions options2 =
                                new FirebaseTranslatorOptions.Builder()
                                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                        .setTargetLanguage(FirebaseTranslateLanguage.DE)
                                        .build();
                        firebaseTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options2);
                        String string2 = dpart.getText().toString();
                        downloadModal(string2);
                        break;
                    case "French":
                        FirebaseTranslatorOptions options3 =
                                new FirebaseTranslatorOptions.Builder()
                                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                        .setTargetLanguage(FirebaseTranslateLanguage.FR)
                                        .build();
                        firebaseTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options3);
                        String string3 = dpart.getText().toString();
                        downloadModal(string3);
                        break;
                    case "Urdu":
                        FirebaseTranslatorOptions options4 =
                                new FirebaseTranslatorOptions.Builder()
                                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                        .setTargetLanguage(FirebaseTranslateLanguage.UR)
                                        .build();
                        firebaseTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options4);
                        String string4 = dpart.getText().toString();
                        downloadModal(string4);
                        break;
                    case "Japanese":
                        FirebaseTranslatorOptions options5 =
                                new FirebaseTranslatorOptions.Builder()
                                        .setSourceLanguage(FirebaseTranslateLanguage.EN)
                                        .setTargetLanguage(FirebaseTranslateLanguage.JA)
                                        .build();
                        firebaseTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options5);
                        String string5 = dpart.getText().toString();
                        downloadModal(string5);
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void downloadModal(final String input) {
        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder().requireWifi().build();
        firebaseTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(translate.this,"Modal is been downloading",Toast.LENGTH_SHORT).show();
                translateLanguage(input);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(translate.this,"Failed to download",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void translateLanguage(String input) {
        firebaseTranslator.translate(input).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                dpart.setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(translate.this,"Fail to tranlate",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ClickMenu(View view) {
        //open drawer
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        //open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);

    }

    public void ClickLogo(View view) {
        //close drawer
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //when drawer is open close it
            drawerLayout.closeDrawer(GravityCompat.START);

        }
    }
    public void Clicklogout(View view)
    {
        logout();
    }

    public void logout() {
        //recreate activity
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        auth.signOut();
        finish();

        Intent intent = new Intent(translate.this, login.class);
        Toast.makeText(translate.this, "Logged Out Successfully.", Toast.LENGTH_LONG).show();
        startActivity(intent);

    }


    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.image) {
            Intent intent = new Intent(translate.this, MainActivity.class);
            Toast.makeText(this, "Capture Image", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.profile) {
            Intent intent = new Intent( translate.this, Profile.class);
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.changepassword) {
            Intent intent = new Intent(translate.this, password.class);
            Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.translate) {
            Intent intent = new Intent(translate.this,translate.class);
            Toast.makeText(this, "Translation", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        return false;
    }
}