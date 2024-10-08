package com.example.onfood;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private List<Item> itemList;
    private OnAddToCartListener addToCartListener;
    private CartManager cartManager;

    // Interface to notify activity when an item is added to the cart
    public interface OnAddToCartListener {
        void onAddToCart(Item item);
    }

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
        this.cartManager = cartManager;
        this.addToCartListener = addToCartListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.bind(item);

        // Get the quantity of the item from CartManager and display it
        int quantity = cartManager.getItemQuantity(item);
        holder.itemQuantity.setText("Quantity: " + quantity);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemPrice, itemQuantity;
        private ImageView itemImage;
        private Button buttonAddToCart;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.foodName);
            itemPrice = itemView.findViewById(R.id.price);
            itemImage = itemView.findViewById(R.id.foodImage);
            itemQuantity = itemView.findViewById(R.id.quantity);  // Added quantity TextView
            buttonAddToCart = itemView.findViewById(R.id.addToCart);
        }

        public void bind(Item item) {
            itemName.setText(item.getName());
            itemPrice.setText("Price: $" + item.getPrice());
            itemQuantity.setText("Quantity: " + cartManager.getItemQuantity(item));

            // Load image using Glide
            Glide.with(itemImage.getContext()).load(item.getImageUrl()).into(itemImage);

            // Ensure that we clear and set a single click listener
            buttonAddToCart.setOnClickListener(v -> {
                // Disable button to avoid double-click issues
                buttonAddToCart.setEnabled(false);

                Log.d("ItemAdapter", "Add to Cart clicked for item: " + item.getName());

                // Ensure item ID is valid before proceeding
                if (item.getId() != null) {
                    // Add one item to the cart
                    cartManager.addToCart(item);  // Add exactly 1 quantity to the cart
                    addToCartListener.onAddToCart(item);  // Notify the activity

                    Toast.makeText(itemView.getContext(), "Added 1 item to Cart", Toast.LENGTH_SHORT).show();

                    // Update the displayed quantity in the item
                    int updatedQuantity = cartManager.getItemQuantity(item);
                    itemQuantity.setText("Quantity: " + updatedQuantity);

                } else {
                    Toast.makeText(itemView.getContext(), "Error: Item ID is null", Toast.LENGTH_SHORT).show();
                }

                // Re-enable button after a short delay to prevent multiple clicks
                buttonAddToCart.postDelayed(() -> buttonAddToCart.setEnabled(true), 500);
            });
        }
    }
}
