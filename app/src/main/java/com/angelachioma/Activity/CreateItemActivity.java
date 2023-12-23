package com.angelachioma.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.angelachioma.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class CreateItemActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextItemName;
    private EditText editTextItemDescription;
    private EditText editTextItemPrice;
    private EditText editTextItemLocation;
    private Button createItemButton;
    private ImageView imageViewItem;
    private DatabaseReference databaseReference;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_item);

        editTextItemName = findViewById(R.id.editTextItemName);
        editTextItemDescription = findViewById(R.id.editTextItemDescription);
        editTextItemPrice = findViewById(R.id.editTextItemPrice);
        editTextItemLocation = findViewById(R.id.editTextLocation);
        createItemButton = findViewById(R.id.buttonCreateItem);
        imageViewItem = findViewById(R.id.imageViewItem);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        databaseReference = FirebaseDatabase.getInstance().getReference().child("items");

        imageViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the image picker when the image is clicked
                openImagePicker();
            }
        });

        if (createItemButton != null) {
            createItemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String itemName = editTextItemName.getText().toString().trim();
                    String itemDescription = editTextItemDescription.getText().toString().trim();
                    String itemPrice = editTextItemPrice.getText().toString().trim();
                    String itemLocation = editTextItemLocation.getText().toString().trim();

                    if (!itemName.isEmpty() && !itemDescription.isEmpty() && !itemPrice.isEmpty() && !itemLocation.isEmpty()) {
                        createNewItem(itemName, itemDescription, itemPrice, itemLocation);
                    } else {
                        showSnackbar("Please fill in all fields");
                    }
                }
            });
        } else {
            showSnackbar("Button not found");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createNewItem(String itemName, String itemDescription, String itemPrice, String itemLocation) {
        DatabaseReference newItemReference = databaseReference.push();
        String itemId = newItemReference.getKey();
        ListItem newItem = new ListItem(itemName, itemDescription, itemPrice, itemLocation, false, null);
        newItem.setId(itemId);

        if (selectedImageUri != null) {
            // Upload the image to Firebase Storage
            uploadImage(selectedImageUri, newItem, newItemReference);
        } else {
            // If no image is selected, save the item details without an image URL
            newItemReference.setValue(newItem);
            showSnackbar("Item created successfully");
            finish();
        }
    }

    private void uploadImage(Uri imageUri, ListItem newItem, DatabaseReference newItemReference) {
        // Get a reference to the Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("item_images");

        // Create a unique name for the image using the item ID
        String imageName = newItem.getId() + ".jpg";
        StorageReference imageRef = storageRef.child(imageName);

        // Upload the image to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // If the image upload is successful, get the image URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save the image URL along with other item details
                        newItem.setImageUrl(uri.toString());

                        // Save the item details to the Realtime Database
                        newItemReference.setValue(newItem);

                        showSnackbar("Item created successfully");
                        finish();
                    });
                })
                .addOnFailureListener(e -> {
                    // If the image upload fails, show an error message
                    showSnackbar("Image upload failed: " + e.getMessage());
                });
    }


    private void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void back(View view) {
        finish();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageViewItem.setImageURI(selectedImageUri);
        }
    }
}
