package com.example.onfood;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> implements CartChangeListener {
    private List<Item> itemList;
    private CartManager cartManager;
    private OnAddToCartListener addToCartListener;

    public interface OnAddToCartListener {
        void onAddToCart(Item item);
    }

    public ItemAdapter(List<Item> itemList, CartManager cartManager, OnAddToCartListener addToCartListener) {
        this.itemList = itemList;
        this.cartManager = cartManager;
        this.addToCartListener = addToCartListener;
        this.cartManager.registerCartChangeListener(this); // Register listener
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
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemPrice, itemQuantity, itemDescription;
        private ImageView itemImage;
        private Button buttonAddToCart;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.foodName);
            itemPrice = itemView.findViewById(R.id.price);
            itemImage = itemView.findViewById(R.id.foodImage);
            itemQuantity = itemView.findViewById(R.id.quantity);
            itemDescription = itemView.findViewById(R.id.description);
            buttonAddToCart = itemView.findViewById(R.id.addToCart);
        }

        public void bind(Item item) {
            itemName.setText(item.getName());
            itemPrice.setText("Price: " + item.getPrice());
            itemDescription.setText(item.getDescription());

            updateQuantityDisplay(item);

            Glide.with(itemImage.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.image)  // Set a placeholder
                    .error(R.drawable.ic_cart)        // Set an error image
                    .into(itemImage);

            buttonAddToCart.setOnClickListener(v -> {
                buttonAddToCart.setEnabled(false);
                cartManager.addToCart(item);
                if (addToCartListener != null) {
                    addToCartListener.onAddToCart(item);
                }
                updateQuantityDisplay(item);
                buttonAddToCart.postDelayed(() -> buttonAddToCart.setEnabled(true), 500);
            });
        }

        private void updateQuantityDisplay(Item item) {
            int quantity = cartManager.getItemQuantity(item);
            int stockQuantity = item.getQuantity();

            if (stockQuantity > 0) {
                itemQuantity.setText("Quantity: " + quantity);
                buttonAddToCart.setVisibility(View.VISIBLE);
                Log.d("ItemAdapter"+item.getName(), "quantity "+stockQuantity);
            } else {
                itemQuantity.setText("Out of Stock");
                buttonAddToCart.setVisibility(View.GONE);
                Log.d("ItemAdapter"+item.getName(), "quantity "+stockQuantity);


            }
        }
    }

    @Override
    public void onCartChanged() {
        notifyDataSetChanged(); // Notify data change to refresh views
    }
}