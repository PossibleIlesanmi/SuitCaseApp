package com.angelachioma.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.angelachioma.R;
import com.angelachioma.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView GotoText;
    private TextView errorMessage;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    Util util;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        util  =  new Util();
        util.initLoader(this);
        buttonLogin = findViewById(R.id.buttonLogin);
        GotoText = findViewById(R.id.Goto);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        errorMessage = findViewById(R.id.errorMessage);
        errorMessage.setVisibility(View.INVISIBLE);
        auth = FirebaseAuth.getInstance();

        GotoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                util.showLoading();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    errorMessage.setText("Please fill in all fields");
                    errorMessage.setTextColor(Color.RED);
                    errorMessage.setVisibility(View.VISIBLE);
                    util.hideLoading();
                    return;
                }


                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Hide the ProgressBar

                                if (task.isSuccessful()) {
                                            // Login successful, navigate to the home screen.
                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish(); // Close the login activity
                                            util.hideLoading();
                                } else {
                                    // Display an error message for incorrect email or password
                                    errorMessage.setText("Incorrect email or password");
                                    errorMessage.setTextColor(Color.RED);
                                    errorMessage.setVisibility(View.VISIBLE);
                                    util.hideLoading();
                                    // Show a Snackbar with the same error message
                                    showSnackbar("Incorrect email or password");
                                }
                            }
                        });
            }
        });
    }

    private void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
