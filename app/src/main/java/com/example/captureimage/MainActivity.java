package com.example.captureimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.captureimage.display;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "camera";
    FirebaseAuth mAuth;
    ImageView imageView;
    DrawerLayout drawerLayout;
    TextView textView;
    GoogleSignInClient googleSignInClient;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInAccount googleSignInAccount;
    File photoFile = null;
    static final int CAPTURE_IMAGE_REQUEST = 1;
    FileOutputStream outputStream;
    ActionBarDrawerToggle actionBarDrawerToggle;

    private static final String IMAGE_DIRECTORY_NAME = "Image";
    private long backPressedTime;
    private Toast backToast;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
     //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


            googleSignInClient = GoogleSignIn.getClient(this, gso);

        //find imageview

        imageView = findViewById(R.id.imageView);
//find textview
        textView = findViewById(R.id.textView);


//check app level permission is granted for Camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    101);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 101);


            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        assert data != null;
        Bundle bundle = data.getExtras();
//from bundle, extract the image
        Bitmap bitmap = (Bitmap) bundle.get("data");
//set image in imageview
        imageView.setImageBitmap(bitmap);
//process the image
//1. create a FirebaseVisionImage object from a Bitmap object
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
//2. Get an instance of FirebaseVision
        FirebaseVision firebaseVision = FirebaseVision.getInstance();
//3. Create an instance of FirebaseVisionTextRecognizer
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
//4. Create a task to process the image
        Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);
//5. if task is success
        task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {

                String s = firebaseVisionText.getText();
                Intent intent = new Intent(MainActivity.this, display.class);
                intent.putExtra("", s);
                startActivity(intent);


            }
        });

//6. if task is failure
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {


        if(backPressedTime + 2000 > System.currentTimeMillis() )
        {
            backToast.cancel();
            super.onBackPressed();
            return;
        }
        else
        {
            backToast =  Toast.makeText(getBaseContext(),"Press back to exit",Toast.LENGTH_SHORT);
            backToast.show();
        }
        backPressedTime = System.currentTimeMillis();

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
    public void ClickImage(View view) {
        //recreate activity
       recreate();

    }
    public void ChangePassword(View view){
        //recreate activity
        change();

    }
    public void Profile(View view) {
        //recreate activity
        redirectActivity(this, Profile.class);
    }
    public static void redirectActivity(Activity activity, Class aClass) {
        //initialized intent
        Intent intent = new Intent(activity, aClass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //start activity
        activity.startActivity(intent);

    }

    public void Clicklogout(View view)
    {
        logout();
    }

    public void logout() {
        mAuth.signOut();
        finish();

        Intent intent = new Intent(MainActivity.this, login.class);
        Toast.makeText(MainActivity.this, "Logged Out Successfully.", Toast.LENGTH_LONG).show();
        startActivity(intent);

    }




    private void change() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                MainActivity.this);
        builder.setTitle("Change Password");
        builder.setMessage("Are You logged in Via Google?");
               /* builder.setNeutralButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Toast.makeText(getApplicationContext(), "Cancel is clicked", Toast.LENGTH_LONG).show();
                            }
                        });*/
        builder.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Toast.makeText(getApplicationContext(), "Cant change Password for google login", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                });
        builder.setPositiveButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Intent intent = new Intent(MainActivity.this, password.class);
                        startActivity(intent);
                    }
                });


        builder.show();

    }

}










