package com.example.onfood;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class HorizontalItemAdapter extends RecyclerView.Adapter<HorizontalItemAdapter.HorizontalItemViewHolder> {

    private List<Item> itemList;

    public HorizontalItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public HorizontalItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sp_item_layout, parent, false);
        return new HorizontalItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class HorizontalItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView itemName, itemPrice;

        public HorizontalItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
        }

        public void bind(Item item) {
            itemName.setText(item.getName());
            itemPrice.setText("Price: $" + item.getPrice());
            Glide.with(itemImage.getContext())
                    .load(item.getImageUrl())
                    .into(itemImage); // Load image using Glide
        }
    }
}