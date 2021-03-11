package com.example.captureimage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class Register extends AppCompatActivity {

    private static final String TAG = "Login";
    EditText email, password,name;
    Button login;
    TextView loginalready;
    String userID;
    FirebaseAuth auth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = findViewById(R.id.edtname);
        email = findViewById(R.id.edtemail);
        password = findViewById(R.id.edtpass);

        login = findViewById(R.id.login);
        loginalready = findViewById(R.id.loginalready);
        fStore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        loginalready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in= new Intent(Register.this,login.class);
                startActivity(in);
            }
        });
       /* if (auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }*/

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
              final String Name = name.getText().toString().trim();
                final String Email = email.getText().toString().trim();
                String Password = password.getText().toString().trim();


                // register the user in firebase
                if (TextUtils.isEmpty(Name)) {
                    name.setError("Enter Your Name");
                    return;
                }
                if (TextUtils.isEmpty(Email)) {
                    email.setError("Email is Required.");
                    return;
                }
                if (TextUtils.isEmpty(Password)) {
                    password.setError("Password is Required.");
                    return;
                }

                auth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                            if (task.isSuccessful()) {
                                               //validatePassword();
                                                Toast.makeText(Register.this, "Successfully Registered, Upload complete!", Toast.LENGTH_SHORT).show();
                                                finish();
                                                startActivity(new Intent(Register.this, login.class));
                                                userID = auth.getCurrentUser().getUid();
                                                DocumentReference documentReference = fStore.collection("users").document(userID);
                                                Map<String,Object> user = new HashMap<>();
                                                user.put("Name",Name);
                                                user.put("Email",Email);
                                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG,"Data is added"+userID);

                                                    }
                                                });

                                              startActivity(new Intent(getApplicationContext(),login.class));
                                            }

                                 else {
                                    Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                 }
                            }
                        });

            }
    });
        }
  /*  private boolean validatePassword() {
        String passwordInput = password.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            password.setError("Field can not be empty");
            return false;
        }


        else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            password.setError("Password is too weak");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }
    private boolean validateEmail() {
        String emailInput = email.getText().toString().trim();
        if (emailInput.isEmpty()) {
            email.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            email.setError("Please enter a valid email address");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }
    private boolean validateUsername() {
        String usernameInput = name.getText().toString().trim();
        if (usernameInput.isEmpty()) {
            name.setError("Field can't be empty");
            return false;
        } else if (usernameInput.length() > 15) {
            name.setError("Username too long");
            return false;
        } else {
            name.setError(null);
            return true;
        }
    }
  public void confirmInput(View v) {
        if (!validateEmail() | !validateUsername() | !validatePassword()) {
            return;
        }
        String input = "Email: " + email.getText().toString();
        input += "\n";
        input += "Username: " + name.getText().toString();
        input += "\n";
        input += "Password: " + password.getText().toString();
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
    }*/
    }




