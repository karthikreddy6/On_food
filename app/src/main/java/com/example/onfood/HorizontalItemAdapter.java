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

public class HorizontalItemAdapter extends RecyclerView.Adapter<HorizontalItemAdapter.HorizontalItemViewHolder> implements CartChangeListener {

    private List<Item> itemList;
    private CartManager cartManager;
    private ItemAdapter.OnAddToCartListener addToCartListener;

    public HorizontalItemAdapter(List<Item> itemList, CartManager cartManager, ItemAdapter.OnAddToCartListener addToCartListener) {
        this.itemList = itemList;
        this.cartManager = cartManager;
        this.addToCartListener = addToCartListener;
        this.cartManager.registerCartChangeListener(this); // Register listener
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
        holder.bind(item); // Bind the item to the ViewHolder
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
            itemQuantity = itemView.findViewById(R.id.quantity);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
        }

        public void bind(Item item) {
            itemName.setText(item.getName());
            itemPrice.setText("Price: $" + item.getPrice());
            Glide.with(itemImage.getContext())
                    .load(item.getImageUrl())
                    .into(itemImage); // Load image using Glide

            updateQuantityDisplay(item); // Update the quantity display based on cart state

            addToCartButton.setOnClickListener(v -> {
                cartManager.addToCart(item); // Add item to cart
                updateQuantityDisplay(item); // Update the quantity display after adding to cart
                if (addToCartListener != null) {
                    addToCartListener.onAddToCart(item); // Notify listener if needed
                }
            });
        }

        private void updateQuantityDisplay(Item item) {
            int quantity = cartManager.getItemQuantity(item); // Get the current quantity in the cart
            if (quantity > 0) {
                itemQuantity.setVisibility(View.VISIBLE);
                itemQuantity.setText(String.valueOf(quantity)); // Show the quantity
                addToCartButton.setVisibility(View.GONE); // Hide the Add to Cart button
            } else {
                itemQuantity.setVisibility(View.GONE);
                addToCartButton.setVisibility(View.VISIBLE); // Show the Add to Cart button if not in cart
            }
        }
    }

    @Override
    public void onCartChanged() {
        notifyDataSetChanged(); // Notify data change to refresh views
    }
}