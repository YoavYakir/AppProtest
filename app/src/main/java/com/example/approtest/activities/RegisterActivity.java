package com.example.approtest.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.approtest.R;
import com.example.approtest.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {


    TextInputEditText editTextEmail, editTextPassword, editTextFirstName, editTextSurname;
    Button buttonRegister;
    FirebaseAuth mAuth;

    FirebaseFirestore db;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.putExtra("FirebaseUser", currentUser);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextEmail = (TextInputEditText) findViewById(R.id.register_email);
        editTextPassword = (TextInputEditText) findViewById(R.id.register_password);
        editTextFirstName = (TextInputEditText) findViewById(R.id.register_first_name);
        editTextSurname = (TextInputEditText) findViewById(R.id.register_surname);
        buttonRegister = (Button) findViewById(R.id.register_button);
        TextView textViewLogin = (TextView) findViewById(R.id.goto_login_text);

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish(); // This prevents the user from going back to the register page
            }
        }
        );



        ProgressBar progressBar = (ProgressBar) findViewById(R.id.register_progress_bar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String email = String.valueOf(editTextEmail.getText()).trim();
                String password = String.valueOf(editTextPassword.getText()).trim();
                String firstName = String.valueOf(editTextFirstName.getText()).trim();
                String surname = String.valueOf(editTextSurname.getText()).trim();

                if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || surname.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);

                    if (TextUtils.isEmpty(firstName)) {
                        editTextFirstName.setError("First name is required");
                        editTextFirstName.requestFocus();
                    }

                    if (TextUtils.isEmpty(surname)) {
                        editTextSurname.setError("Surname is required");
                        editTextSurname.requestFocus();
                    }

                    if (TextUtils.isEmpty(email)) {
                        editTextEmail.setError("Email is required");
                        editTextEmail.requestFocus();
                    }

                    if (TextUtils.isEmpty(password)) {
                        editTextPassword.setError("Password is required");
                        editTextPassword.requestFocus();
                    }

                    return;

                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(RegisterActivity.this, "Account created successfully",
                                            Toast.LENGTH_SHORT).show();
                                    DocumentReference documentReference = db.collection("users").document(mAuth.getCurrentUser().getUid()); // TODO: throw exception if user is null
                                    User user = new User(firstName+" "+surname,mAuth.getCurrentUser().getUid(),email);
                                    System.out.println("User: " + user);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "onSuccess: user profile is created for " + user);
                                        }
                                    });
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });

    }
}