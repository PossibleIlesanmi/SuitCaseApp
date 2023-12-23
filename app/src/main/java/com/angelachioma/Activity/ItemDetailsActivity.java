package com.angelachioma.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.angelachioma.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

public class ItemDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // Retrieve the item's details from the intent
        Intent intent = getIntent();
        String itemName = intent.getStringExtra("item_name");
        String itemDescription = intent.getStringExtra("item_description");
        String itemPrice = intent.getStringExtra("item_price");
        String itemLocation = intent.getStringExtra("item_location");
        String imageUrl = intent.getStringExtra("item_image_url"); // Add this line

        // Find the TextView and ImageView elements in the layout
        TextView itemNameTextView = findViewById(R.id.textItemName);
        TextView itemDescriptionTextView = findViewById(R.id.textItemDescription);
        TextView itemPriceTextView = findViewById(R.id.textItemPrice);
        TextView itemLocationTextView = findViewById(R.id.textItemLocation);
        ImageView itemImageView = findViewById(R.id.imageViewItemDetails); // Add this line

        // Set the text of the TextView elements with the item's details
        itemNameTextView.setText(itemName);
        itemDescriptionTextView.setText(itemDescription);
        itemPriceTextView.setText(itemPrice);
        itemLocationTextView.setText(itemLocation);

        // Debug log for imageUrl
        Log.d("ItemDetailsActivity", "Image URL: " + imageUrl);

        // Load the image using Glide if imageUrl is not null
        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_image))
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Log the error if the image loading fails
                            Log.e("ItemDetailsActivity", "Image loading failed", e);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            // Log success or perform any additional actions
                            Log.d("ItemDetailsActivity", "Image loading successful");
                            return false;
                        }
                    })
                    .into(itemImageView);
        } else {
            // Handle the case where imageUrl is null, e.g., show a placeholder image
            itemImageView.setImageResource(R.drawable.ic_image);
        }
    }

    public void back(View view) {
        finish();
    }
}
