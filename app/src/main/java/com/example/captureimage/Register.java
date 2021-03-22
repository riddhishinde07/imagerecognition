package com.example.captureimage;



import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;

public class Register extends AppCompatActivity {

    EditText email, password,name ;
    Button SignUp,login;
    String userID;
    DatabaseReference reference;
    // Creating string to hold email and password .
    String EmailHolder, PasswordHolder,NameHolder ;
    ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;
    // Creating FirebaseAuth object.
    FirebaseAuth firebaseAuth ;
    FirebaseFirestore firestore;
    private static final String TAG = "Login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

// Assigning layout email ID and Password ID.
        name=(EditText)findViewById(R.id.edtname);
        email = (EditText)findViewById(R.id.edtemail);
        password = (EditText)findViewById(R.id.edtpass);
      //  reference = FirebaseDatabase.getInstance().getReference().child("Members");
// Assign button layout ID.
        SignUp = (Button)findViewById(R.id.login);

// Creating object instance.
        firebaseAuth = FirebaseAuth.getInstance();
        firestore =FirebaseFirestore.getInstance();

// Adding click listener to Sign Up Button.
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NameHolder = name.getText().toString().trim();
                EmailHolder = email.getText().toString().trim();
                PasswordHolder= password.getText().toString().trim();

                if (TextUtils.isEmpty(NameHolder)) {
                    name.setError("Enter Your Name");
                    return;
                }
                if (TextUtils.isEmpty(EmailHolder)) {
                    email.setError("Email is Required.");
                    return;
                }
                if (TextUtils.isEmpty(PasswordHolder)) {
                    password.setError("Password is Required.");
                    return;
                }
// register the user in firebase

                firebaseAuth.createUserWithEmailAndPassword(EmailHolder, PasswordHolder)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    Toast.makeText(Register.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), login.class));
                                    userID = firebaseAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = firestore.collection("users").document(userID);
                                    Map<String,Object> user = new HashMap<>();
                                    user.put("Name",NameHolder);
                                    user.put("Email",EmailHolder);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG,"Data is added"+userID);

                                        }
                                    });
                                    startActivity(new Intent(getApplicationContext(), login.class));
                                }

                                else {
                                    Toast.makeText(Register.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

            }
        });

       TextView login=findViewById(R.id.loginalready);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Register.this, login.class);
                startActivity(intent);
                finish();


            }
        });
    }

}


