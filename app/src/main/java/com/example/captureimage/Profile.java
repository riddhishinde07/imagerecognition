package com.example.captureimage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;
import java.util.regex.Pattern;

public class Profile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView name,name1,email1,email,text,change,only;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userID,userID1;
    ImageView profile,profile1,add;
    DrawerLayout drawerLayout;
    StorageReference storageReference;
    GoogleSignInOptions gso;
    GoogleSignInAccount account;
    GoogleSignInClient mGoogleSignInClient;
    LinearLayout linearLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    SharedPreferences sharedPreferences;
    boolean getLoginStatus;

    private NavigationView nv;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint({"SetTextI18n", "CheckResult", "CutPasteId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        name = findViewById(R.id.name);
        text = findViewById(R.id.text);
        profile = findViewById(R.id.profile);
        profile1 = findViewById(R.id.profile1);
        email = findViewById(R.id.email);
        name1 = findViewById(R.id.name1);
        email1 = findViewById(R.id.email1);
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        drawerLayout= findViewById(R.id.drawer_layout);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        NavigationView navigationView = (NavigationView)findViewById(R.id.nv);
         navigationView.setNavigationItemSelectedListener(this);
        sharedPreferences = getSharedPreferences("googleLogin", Context.MODE_PRIVATE);
        getLoginStatus = sharedPreferences.getBoolean("googleLogin", false);
        if(getLoginStatus){
            navigationView.getMenu().removeItem(R.id.changepassword);
            profile1.setVisibility(View.INVISIBLE);

        }
        userID = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        user = auth.getCurrentUser();
       // userID1 = auth.getCurrentUser().getUid();
        Glide.with(this)
                .load(user.getPhotoUrl())
                .into(profile);
        name1.setText(user.getDisplayName());
        email1.setText(user.getEmail());



        storageReference = FirebaseStorage.getInstance().getReference();



        //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();
        StorageReference profileRef = storageReference.child("users/" + auth.getCurrentUser().getUid() + "profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profile1);
            }
        });


        profile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, 1000);
            }
        });


       DocumentReference documentReference1 = fStore.collection("users").document(userID);
        documentReference1.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                name.setText(documentSnapshot.getString("Name"));
                email.setText(documentSnapshot.getString("Email"));

            }
        });

    }


    /*private void change() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                Profile.this);
        builder.setTitle("Change Password");
        builder.setMessage("Are You logged in Via Google?");
               /* builder.setNeutralButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                Toast.makeText(getApplicationContext(), "Cancel is clicked", Toast.LENGTH_LONG).show();
                            }
                        });*/
      /*  builder.setNegativeButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Toast.makeText(getApplicationContext(), "Cant change Password for google login", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Profile.this, Profile.class);
                        startActivity(intent);
                    }

                });
        builder.setPositiveButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Intent intent = new Intent(Profile.this, password.class);
                        startActivity(intent);
                    }
                });


        builder.show();

    }*/

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.image) {
            Intent intent = new Intent(Profile.this, MainActivity.class);
            Toast.makeText(this, "Capture Image", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.profile) {
            Intent intent = new Intent(Profile.this, Profile.class);
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.changepassword) {
            Intent intent = new Intent(Profile.this, password.class);
            Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }


        return false;
    }

    protected void onActivityResult(int requestCode,int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                //  profile.setImageURI(imageUri);
                uploadImage(imageUri);

            }
        }
    }

    private void uploadImage(Uri imageUri) {
        //upload image to firebase
        final StorageReference fileRef = storageReference.child("users/"+auth.getCurrentUser().getUid()+"profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        Picasso.get().load(uri).into(profile1);

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this,"Fail",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ClickMenu(View view){
        //open drawer
        openDrawer(drawerLayout);
    }

    public static void  openDrawer(DrawerLayout drawerLayout) {
        //open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);

    }

    public void ClickLogo(View view){
        //close drawer
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            //when drawer is open close it
            drawerLayout.closeDrawer(GravityCompat.START);

        }
    }

    public void Clicklogout(View view)
    {
        logout();
    }
    public  void logout(){

        auth.signOut();
        finish();

        Intent intent = new Intent(Profile.this, login.class);
        Toast.makeText(Profile.this, "Logged Out Successfully.", Toast.LENGTH_LONG).show();
        startActivity(intent);

    }
    public void ClickImage(View view){
        //recreate activity
        redirectActivity(this,MainActivity.class);

    }
    public void Profile(View view){
        //recreate activity
        recreate();
    }

    public void ChangePassword(View view){
        //recreate activity

    }


    public static void redirectActivity(Activity activity, Class aClass) {
        //initialized intent
        Intent intent = new Intent(activity,aClass);
        //set flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //start activity
        activity.startActivity(intent);

    }



    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }

}
