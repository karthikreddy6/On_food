package com.example.onfood;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class HorizontalItemAdapter extends RecyclerView.Adapter<HorizontalItemAdapter.HorizontalItemViewHolder> {

    private List<Item> itemList;
    private CartManager cartManager;
    private ItemAdapter.OnAddToCartListener addToCartListener;

    public HorizontalItemAdapter(List<Item> itemList, CartManager cartManager, ItemAdapter.OnAddToCartListener addToCartListener) {
        this.itemList = itemList;
        this.cartManager = cartManager;
        this.addToCartListener = addToCartListener;
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

    class HorizontalItemViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;
        private TextView itemName, itemPrice, itemQuantity;
        private ImageButton addToCartButton;

        public HorizontalItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemName = itemView.findViewById(R.id.itemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            itemQuantity = itemView.findViewById(R.id.quantity); // Assuming you have this TextView in sp_item_layout
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }

        public void bind(Item item) {
            itemName.setText(item.getName());
            itemPrice.setText("Price: $" + item.getPrice());
            Glide.with(itemImage.getContext())
                    .load(item.getImageUrl())
                    .into(itemImage); // Load image using Glide

            // Set initial quantity visibility
            int quantity = cartManager.getItemQuantity(item);
            if (quantity > 0) {
                itemQuantity.setVisibility(View.VISIBLE);
                itemQuantity.setText(String.valueOf(quantity));
                addToCartButton.setVisibility(View.GONE); // Hide button if item is already in cart
            } else {
                itemQuantity.setVisibility(View.GONE);
                addToCartButton.setVisibility(View.VISIBLE); // Show button if item is not in cart
            }

            addToCartButton.setOnClickListener(v -> {
                cartManager.addToCart(item);
                itemQuantity.setVisibility(View.VISIBLE); // Show quantity TextView
                itemQuantity.setText(String.valueOf(cartManager.getItemQuantity(item))); // Update quantity
                addToCartButton.setVisibility(View.GONE); // Hide the button after adding to cart

                if (addToCartListener != null) {
                    addToCartListener.onAddToCart(item);
                }
            });
        }
    }
}