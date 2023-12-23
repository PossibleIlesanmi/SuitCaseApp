package com.angelachioma.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.angelachioma.Adapter.ItemAdapter;
import com.angelachioma.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements ItemAdapter.OnItemClickListener, ItemAdapter.OnSwipeListener {

    private FloatingActionButton fabCreateItem;
    private ItemAdapter itemAdapter;
    private DatabaseReference databaseReference;
    private List<ListItem> items;

    private static final int DELETE_ITEM_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        fabCreateItem = findViewById(R.id.fabCreateList);


        RecyclerView recyclerView = findViewById(R.id.recyclerViewLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemAdapter = new ItemAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(itemAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("items");

        ItemTouchHelper.Callback callback = new ItemAdapter.SwipeToDeleteCallback(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


        fabCreateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CreateItemActivity.class);
                startActivity(intent);
            }
        });

        items = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();

                double totalCost = 0;

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    ListItem item = itemSnapshot.getValue(ListItem.class);
                    if (item != null && item.getPrice() != null) {
                        items.add(item);
                        double itemPrice = Double.parseDouble(item.getPrice());
                        totalCost += itemPrice;
                    }
                }

                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle("Total: $" + totalCost);
                    actionBar.setSubtitle(null);
                }

                itemAdapter.setItems(items);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position, View view) {
        ListItem item = items.get(position);
        showItemDetails(item);
    }

    @Override
    public void onThreeDotsClick(int position, View view) {
        ListItem item = items.get(position);
        showPopupMenu(item, position, view);
    }


    @Override
    public void onSwipeLeft(int position) {
        if (itemAdapter != null) {
            ListItem item = items.get(position);
            showDeleteConfirmationDialog(item, position);
        }
    }


    @Override
    public void onSwipeRight(int position) {
        if (itemAdapter != null) {
            ListItem item = items.get(position);
            editItem(item);
        }
    }

    private void showItemDetails(ListItem item) {
        Intent intent = new Intent(this, ItemDetailsActivity.class);
        intent.putExtra("item_name", item.getName());
        intent.putExtra("item_description", item.getDescription());
        intent.putExtra("item_price", item.getPrice());
        intent.putExtra("item_location", item.getLocation());
        intent.putExtra("item_image_url", item.getImageUrl());
        startActivity(intent);
    }

    private void showPopupMenu(ListItem item, int position, View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add("Edit");
        popupMenu.getMenu().add("Delete");
        popupMenu.getMenu().add("Share");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                String title = menuItem.getTitle().toString();
                if (title.equals("Edit")) {
                    editItem(item);
                } else if (title.equals("Delete")) {
                    showDeleteConfirmationDialog(item, position);
                } else if (title.equals("Share")) {
                    sendSms(item);
                }
                return true;
            }
        });

        popupMenu.show();
    }

    private void editItem(ListItem item) {
        String itemId = item.getId();
        Intent editIntent = new Intent(HomeActivity.this, EditItemActivity.class);
        editIntent.putExtra("id", itemId);
        startActivity(editIntent);
    }

    private void showDeleteConfirmationDialog(ListItem item, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Are you sure you want to delete this item?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItem(item, position);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                itemAdapter.notifyItemChanged(position);
            }
        });

        builder.create().show();
    }



    private void deleteItem(ListItem item, int position) {
        String itemId = item.getId();
        databaseReference.child(itemId).removeValue();

    }




    private void sendSms(ListItem listItem) {
        Uri uri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        String sms = "Product Name: " + listItem.getName() + "\n" +
                "Price: " + listItem.getPrice() + "\n" +
                "Location: " + listItem.getLocation() + "\n";

        intent.putExtra("sms_body", sms);
        startActivity(intent);
    }

    public void back(View view) {
        finish();
    }
}
