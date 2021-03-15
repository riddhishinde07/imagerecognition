package com.example.captureimage;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.protobuf.Api;


public class login extends AppCompatActivity  {
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    //code you will assign for starting the new activity.
    private static int RC_SIGN_IN = 1;
    TextView email, password,forgot,signup;
    Button sign_in;
    ImageView profile1;
    FirebaseAuth auth;
    DrawerLayout drawerLayout;
    GoogleSignInAccount googleSignInAccount;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private ActionBarDrawerToggle t;
    private NavigationView nv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sign_in = (Button) findViewById(R.id.sign_in);
        email = (TextView) findViewById(R.id.edtemail);
        password = (TextView) findViewById(R.id.edtpassword);
        profile1 = findViewById(R.id.profile1);
        drawerLayout = findViewById(R.id.drawer_layout);
 //   final TextView change = findViewById(R.id.change);
        //creating object instance
        mAuth = FirebaseAuth.getInstance();
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    //    NavigationView navigationView = (NavigationView)findViewById(R.id.nv);
      //  navigationView.setNavigationItemSelectedListener(this);
        //Use to configure sign in api
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("247322205294-i9qn10pree5fjfupldkkd8m3jqm49gfu.apps.googleusercontent.com")
                .requestEmail()
                .build();

    /*   GoogleSignInAccount signInAccount =  GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount != null){
            startActivity(new Intent(this,MainActivity.class));
        }*/




        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // interact with the Google Sign In API.
        Button signInButton = findViewById(R.id.sign_in_button);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });


        signup = (TextView) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(login.this, Register.class);
                startActivity(in);
            }
        });

        forgot =(TextView) findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(login.this, reset.class);
                startActivity(in);
            }
        });


        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // profile1.setVisibility(View.VISIBLE);
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();


                if (TextUtils.isEmpty(Email)) {
                    email.setError("Email is Required.");
                    return;
                }

                if (TextUtils.isEmpty(Password)) {
                    password.setError("Password is Required.");
                    return;
                }
                //checks the emailid or password in registered or not
                mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));


                        } else {
                            Toast.makeText(login.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }


                });
            }
        });
    }
     protected  void  onStart() {

         super.onStart();
         if(FirebaseAuth.getInstance().getCurrentUser() !=null){
             startActivity(new Intent(getApplicationContext(),MainActivity.class));
             finish();
         }


     }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount signInAcc = signInTask.getResult(ApiException.class);
                AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAcc.getIdToken(),null);
                mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getApplicationContext(), "Logged in Successfully", Toast.LENGTH_SHORT).show();
                       // startActivity(new Intent(getApplicationContext(),MainActivity.class));
                  //      PackageManager pm = getPackageManager();
                    //    pm.setComponentEnabledSetting(new ComponentName(login.this, change.class),
                      //          PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
                        //Toast.makeText(getApplicationContext(), "Change Password Cant be used to google login", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }
  /* public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.image) {
            Intent intent = new Intent(login.this,MainActivity.class);
            Toast.makeText(this, "Capture Image", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.profile) {
            Intent intent = new Intent(login.this,Profile.class);
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        if (id == R.id.changepassword) {
            googleSignInAccount = GoogleSignIn.getLastSignedInAccount(login.this);
            if (googleSignInAccount != null) {

                PackageManager pm = getPackageManager();
                pm.setComponentEnabledSetting(new ComponentName(this, changepass.class),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 0);
                Toast.makeText(getApplicationContext(), "Change Password Cant be used to google login", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                Intent intent = new Intent(login.this, changepass.class);
                Toast.makeText(this, "Change Password", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }
        return false;
    }*/
}