package com.example.captureimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Pattern;

public class password extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    TextView text,show1,show2,show3,show4;
    EditText newpass,oldpass,confirm;
    Button changepass;
    FirebaseAuth auth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    ProgressDialog dialog;
    SharedPreferences sharedPreferences;
    boolean getLoginStatus;

    FirebaseUser user;
    ActionBarDrawerToggle actionBarDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        text = findViewById(R.id.text);
        show1 = findViewById(R.id.show1);
        show2 = findViewById(R.id.show2);
        show3 = findViewById(R.id.show3);
        show4 = findViewById(R.id.show4);
        drawerLayout = findViewById(R.id.drawer_layout);
        newpass = findViewById(R.id.newpass);
        oldpass = findViewById(R.id.oldpass);

        confirm = findViewById(R.id.confirm);
        changepass = findViewById(R.id.changepass);
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
     //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView)findViewById(R.id.nv);
        navigationView.setNavigationItemSelectedListener(this);
        sharedPreferences = getSharedPreferences("googleLogin", Context.MODE_PRIVATE);
        getLoginStatus = sharedPreferences.getBoolean("googleLogin", false);
        if(getLoginStatus){
            navigationView.getMenu().removeItem(R.id.changepassword);
        }
        newpass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newpass1 = newpass.getText().toString();
                validatePassword(newpass1);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        changepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldpass1 = oldpass.getText().toString();
                String newpass1 = newpass.getText().toString();
                String confirm1 = confirm.getText().toString();
                if (TextUtils.isEmpty(oldpass1)) {
                    Toast.makeText(getApplicationContext(), "Enter your current password...", Toast.LENGTH_SHORT).show();
                    return; //don't proceed further
                }
                if (newpass1.length() < 8) {
                    Toast.makeText(getApplicationContext(), "more then 8 character", Toast.LENGTH_SHORT).show();
                    return;  //don't proceed further
                }
                if(!newpass1.equals(confirm1))
                {
                    Toast.makeText(getApplicationContext(), "Your password do not match with your confirm password...", Toast.LENGTH_SHORT).show();
                    return;
                }
                updatePassword(oldpass1, newpass1);
            }
        });
    }

    private void updatePassword(String oldPassword, final String newPassword) {
        //show dialog
        //pd.show();

        //get current user
        final FirebaseUser user = auth.getCurrentUser();

        //before changing password re-authenticate the user
        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //successfully authenticated, begin update

                        user.updatePassword(newPassword)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //password updated
                                        //pd.dismiss();
                                        Toast.makeText(getApplicationContext(), "Password Updated...", Toast.LENGTH_SHORT).show();
                                        auth.signOut();
                                        finish();
                                        Intent intent = new Intent(password.this,login.class);
                                        startActivity(intent);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //failed updating password, show reason
                                        // pd.dismiss();
                                        Toast.makeText(getApplicationContext(), "Password cannot change" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //authentication failed, show reason
                        //              pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Password not change" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void validatePassword(String password) {
        Pattern upper = Pattern.compile("[A-Z]");
        Pattern lower = Pattern.compile("[a-z]");
        Pattern number = Pattern.compile("[0-9]");
        Pattern special = Pattern.compile(".*[@#!$%^&+=].*");


        if (lower.matcher(password).find()) {
            show1.setTextColor(Color.BLACK);
        } else {
            show1.setTextColor(Color.RED);
        }
        if (upper.matcher(password).find()) {
            show2.setTextColor(Color.BLACK);
        } else {
            show2.setTextColor(Color.RED);
        }
        if (number.matcher(password).find()) {
            show3.setTextColor(Color.BLACK);
        } else {
            show3.setTextColor(Color.RED);
        }
        if (special.matcher(password).find()) {
            show4.setTextColor(Color.BLACK);
        } else {
            show4.setTextColor(Color.RED);
        }

    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.image) {
            Intent intent = new Intent(password.this, MainActivity.class);
            Toast.makeText(this, "Capture Image", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.profile) {
            Intent intent = new Intent( password.this, Profile.class);
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.changepassword) {
            Intent intent = new Intent(password.this, password.class);
            Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.translate) {
            Intent intent = new Intent(password.this,translate.class);
            Toast.makeText(this, "Translation", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }


        return false;
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
        redirectActivity(password.this,MainActivity.class);

    }
    public void ChangePassword(View view){
        //recreate activity


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
    public void Logout() {
        //recreate activity
        logout();
    }



    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        auth.signOut();
        finish();

        Intent intent = new Intent(password.this, login.class);
        Toast.makeText(password.this, "Logged Out Successfully.", Toast.LENGTH_LONG).show();
        startActivity(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }

}