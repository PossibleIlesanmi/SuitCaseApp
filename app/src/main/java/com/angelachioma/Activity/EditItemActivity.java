package com.angelachioma.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.angelachioma.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditItemActivity extends AppCompatActivity {

    private EditText editTextItemName;
    private EditText editTextItemDescription;
    private EditText editTextItemPrice;
    private EditText editTextLocation;  // Location field
    private Button saveChangesButton;
    private DatabaseReference databaseReference;
    private String itemId; // Pass the item ID from the previous screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        editTextItemName = findViewById(R.id.editTextItemName);
        editTextItemDescription = findViewById(R.id.editTextItemDescription);
        editTextItemPrice = findViewById(R.id.editTextItemPrice);
        editTextLocation = findViewById(R.id.editTextLocation);  // Location field
        saveChangesButton = findViewById(R.id.buttonSaveChanges);

        // Initialize the Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("items");

        // Retrieve the item ID from the previous screen
        itemId = getIntent().getStringExtra("id");

        // Populate the existing item details
        loadItemDetails();

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = editTextItemName.getText().toString().trim();
                String itemDescription = editTextItemDescription.getText().toString().trim();
                String itemPrice = editTextItemPrice.getText().toString().trim();
                String location = editTextLocation.getText().toString().trim();

                if (!itemName.isEmpty() && !itemDescription.isEmpty() && !itemPrice.isEmpty()) {
                    // Update item details in Firebase
                    updateItemDetails(itemName, itemDescription, itemPrice, location);
                }
            }
        });
    }

    private void loadItemDetails() {
        // Fetch existing item details from Firebase and populate the EditText fields
        databaseReference.child(itemId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // The task succeeded, and you can access the data
                DataSnapshot itemSnapshot = task.getResult();
                if (itemSnapshot != null) {
                    String itemName = itemSnapshot.child("name").getValue(String.class);
                    String itemDescription = itemSnapshot.child("description").getValue(String.class);
                    String itemPrice = itemSnapshot.child("price").getValue(String.class);
                    String location = itemSnapshot.child("location").getValue(String.class);

                    editTextItemName.setText(itemName);
                    editTextItemDescription.setText(itemDescription);
                    editTextItemPrice.setText(itemPrice);
                    editTextLocation.setText(location);
                }
            } else {
                // The task failed, you can handle the error here
                Toast.makeText(EditItemActivity.this, "Error loading item details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateItemDetails(String itemName, String itemDescription, String itemPrice, String location) {
        // Update the item details in Firebase
        databaseReference.child(itemId).child("name").setValue(itemName);
        databaseReference.child(itemId).child("description").setValue(itemDescription);
        databaseReference.child(itemId).child("price").setValue(itemPrice);
        databaseReference.child(itemId).child("location").setValue(location);

        // Display a message to indicate success
        Toast.makeText(EditItemActivity.this, "Item details updated successfully", Toast.LENGTH_SHORT).show();

        // Close the activity and return to the previous screen
        finish();
    }

    public void back(View view) {
        finish();
    }
}
