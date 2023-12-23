package com.angelachioma.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.angelachioma.Activity.ListItem;
import com.angelachioma.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<ListItem> items;
    private OnItemClickListener itemClickListener;
    private DatabaseReference databaseReference;

    private OnSwipeListener swipeListener;

    public ItemAdapter(List<ListItem> items, OnItemClickListener listener, OnSwipeListener swipeListener) {
        this.items = items;
        this.itemClickListener = listener;
        this.swipeListener = swipeListener;
    }

    public ItemAdapter(List<ListItem> items, OnItemClickListener listener) {
        this.items = items;
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ListItem listItem = items.get(position);

        holder.textItemName.setText(listItem.getName());
        holder.textItemDescription.setText(listItem.getDescription());
        holder.textItemPrice.setText(listItem.getPrice());
        holder.textItemLocation.setText(listItem.getLocation());
        holder.checkboxItemPurchased.setChecked(listItem.isPurchased());

        // Use Glide to load the image
        Glide.with(holder.itemView.getContext())
                .load(listItem.getImageUrl())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_add))
                .into(holder.imageViewItem);

        databaseReference = FirebaseDatabase.getInstance().getReference("items");

        // Set a click listener on the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(position, v);
                    }
                }
            }
        });

        holder.checkboxItemPurchased.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateItemPurchasedStatus(listItem.getId(), isChecked);
            }
        });

        holder.imageViewThreeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener.onThreeDotsClick(position, v);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View view);

        void onThreeDotsClick(int position, View view);
    }

    public void setItems(List<ListItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public interface OnSwipeListener {
        void onSwipeLeft(int position);

        void onSwipeRight(int position);
    }

    private void updateItemPurchasedStatus(String itemId, boolean isPurchased) {
        // Get a reference to the item in the database
        DatabaseReference itemRef = databaseReference.child(itemId);

        // Update the "purchased" status
        itemRef.child("purchased").setValue(isPurchased);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textItemName;
        TextView textItemDescription;
        TextView textItemPrice;
        TextView textItemLocation;
        CheckBox checkboxItemPurchased;
        ImageView imageViewThreeDots;
        ImageView imageViewItem;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textItemName = itemView.findViewById(R.id.textItemName);
            textItemDescription = itemView.findViewById(R.id.textItemDescription);
            textItemPrice = itemView.findViewById(R.id.textItemPrice);
            textItemLocation = itemView.findViewById(R.id.textItemLocation1);
            checkboxItemPurchased = itemView.findViewById(R.id.checkboxItemPurchased);
            imageViewThreeDots = itemView.findViewById(R.id.iconThreeDots);
            imageViewItem = itemView.findViewById(R.id.imageViewItem);
        }
    }

    public static class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

        private final OnSwipeListener swipeListener;

        public SwipeToDeleteCallback(OnSwipeListener listener) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.swipeListener = listener;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.LEFT) {
                // Notify the listener about the swipe left action
                swipeListener.onSwipeLeft(position);
            } else if (direction == ItemTouchHelper.RIGHT) {
                // Notify the listener about the swipe right action
                swipeListener.onSwipeRight(position);
            }
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            getDefaultUIUtil().clearView(((ItemViewHolder) viewHolder).itemView);
        }
    }
}
