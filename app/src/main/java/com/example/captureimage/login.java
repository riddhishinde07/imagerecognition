package com.example.captureimage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;


public class login extends AppCompatActivity   {
    private static final String TAG = "";
    FirebaseAuth mAuth;
    Boolean isLoggin = false;
    GoogleSignInClient mGoogleSignInClient;
    //code you will assign for starting the new activity.
    private static int RC_SIGN_IN = 1;
    TextView forgot,signup;
    Button sign_in,sign_facebook,sign_twitter;
    ImageView profile1;
    EditText email, password;
    FirebaseAuth auth;
    DrawerLayout drawerLayout;
    GoogleSignInAccount googleSignInAccount;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    Boolean IsLoggedIn = false;

    CallbackManager callbackManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sign_in = (Button) findViewById(R.id.sign_in);
        email =  findViewById(R.id.edtemail);
        password =  findViewById(R.id.edtpassword);
        profile1 = findViewById(R.id.profile1);
        sign_facebook = findViewById(R.id.sign_in_facebook);
        sign_twitter = findViewById(R.id.sign_in_twitter);
        //   final TextView change = findViewById(R.id.change);
        //creating object instance

        mAuth = FirebaseAuth.getInstance();

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

               signin();

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

        sign_facebook.getInstance().registerCallBack(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        }){

        }

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // profile1.setVisibility(View.VISIBLE);
                String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();
               isLoggin = true;



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

    private void facebook() {
        callbackManager = CallbackManager.Factory.create();
        sign_facebook.setReadPermissions(Arrays.asList("EMAIL"));


    }

    private Boolean validateEmail() {
        String val = email.getText().toString();
        if (val.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else {
            email.setError(null);


            return true;
        }
    }
    private Boolean validatePassword() {
        String val = password.getText().toString();
        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else {
            password.setError(null);

            return true;
        }
    }
    public void loginUser(View view){
        if(!validateEmail() | !validatePassword())
        {
            return;
        }
        else 
        {
           User();
        }
    }

    private void User() {
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();


    }

    private void signin() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        startActivityForResult(signInIntent, RC_SIGN_IN);

        SharedPreferences sharedPreferences = getSharedPreferences("googleLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("googleLogin", true);
        editor.apply();
    }



    protected  void  onStart() {

        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Task<GoogleSignInAccount> signInTask = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount signInAcc = signInTask.getResult(ApiException.class);
            AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAcc.getIdToken(),null);
            mAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Toast.makeText(getApplicationContext(), "Logged in Successfully", Toast.LENGTH_SHORT).show();

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

    private void redirectToHome() {
        startActivity(new Intent(login.this, MainActivity.class));
        finish();
    }
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }




}


