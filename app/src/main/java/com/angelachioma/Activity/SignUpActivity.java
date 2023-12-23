package com.angelachioma.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.angelachioma.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextPassword, editTextFullname;
    private Button buttonSignUp;
    private TextView GotoText;
    private FirebaseAuth auth;
    String userid;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initLoader();
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextFullname = findViewById(R.id.editTextFullname);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        GotoText = findViewById(R.id.Goto);

        GotoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        auth = FirebaseAuth.getInstance();

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    showSnackbar("Please fill in all fields");
                    return;
                }

                if (!isPasswordValid(password)) {
                    showSnackbar("Password must be at least 6 characters");
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Registration successful, save information to database and navigate to the home screen.
                                    submitInformation();
                                    hideLoading();
                                    Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish(); // Close the signup activity.
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hideLoading();
                                TextView errorMessage = findViewById(R.id.errorMessage);
                                errorMessage.setVisibility(View.VISIBLE);
                                errorMessage.setText(e.getMessage());
                                hideLoading();
                                errorMessage.setTextColor(Color.RED);
                            }
                        });;

            }
        });
    }

    private boolean isPasswordValid(String password) {
        // Check if the password is at least 6 characters long
        return password.length() >= 6;
    }

    // Method to show a Snackbar with a message
    private void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content); // Get the parent layout
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show();
    }

    public void back(View view) {
        finish();
    }

    private void submitInformation(){

        String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
                "Oct", "Nov", "Dec"};
        GregorianCalendar gcalendar = new GregorianCalendar();
        String date = months[gcalendar.get(Calendar.MONTH)] + " " + gcalendar.get(Calendar.DATE) + " " + gcalendar.get(Calendar.YEAR);

        userid = FirebaseAuth.getInstance().getUid();
        Map<String, String> map = new HashMap<>();
        map.put("id", userid);
        map.put("name", editTextFullname.getText().toString());
        map.put("date", date);



        FirebaseDatabase.getInstance().getReference().child("Parent").child(FirebaseAuth.getInstance().getUid())
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        hideLoading();
                        editTextFullname.setText("");
                                           }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideLoading();
                        Toast.makeText(getApplicationContext(), "Could not insert", Toast.LENGTH_LONG).show();
                    }
                });


    }



    public void showLoading() {
        dialog.show();
    }

    public void hideLoading() {
        dialog.dismiss();
    }

    public void initLoader() {
        dialog = new Dialog(SignUpActivity.this);
        dialog.setContentView(R.layout.dialogloading);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
