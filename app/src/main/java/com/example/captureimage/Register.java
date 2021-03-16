

package com.example.captureimage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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

                String Name = name.getText().toString();
                String Password = password.getText().toString();
                String Email = email.getText().toString();

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
                                    user.put("Name",name);
                                    user.put("Email",email);
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
}



