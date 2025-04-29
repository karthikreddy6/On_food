package com.example.onfood;
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
import com.example.onfood.Activity.CartActivity;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private CartManager cartManager;
    private CartActivity cartActivity;


    public CartAdapter(List<CartItem> cartItemList, CartManager cartManager, CartActivity cartActivity) {
        this.cartItemList = cartItemList;
        this.cartManager = cartManager;
        this.cartActivity = cartActivity;


    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemQuantity, itemPrice;
        private Button buttonIncreaseQuantity,  buttonDecreaseQuantity;
        private ImageView imageView;
        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.cartItemName);
            itemQuantity = itemView.findViewById(R.id.cartItemQuantity);
            itemPrice = itemView.findViewById(R.id.cartItemPrice);
            imageView =itemView.findViewById(R.id.cartItemImage);
            buttonIncreaseQuantity = itemView.findViewById(R.id.increaseQuantity);
            buttonDecreaseQuantity = itemView.findViewById(R.id.decreaseQuantity);
        }

        public void bind(CartItem cartItem) {
            Item item = cartItem.getItem();
            itemName.setText(item.getName() != null ? item.getName() : "Unknown Item");
            itemQuantity.setText(String.valueOf(cartItem.getQuantity()));
            itemPrice.setText("Price: ₹" + item.getPrice() * cartItem.getQuantity());

            Glide.with(itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.image)  // Set a placeholder
                    .error(R.drawable.ic_cart)        // Set an error image
                    .into(imageView);

            buttonIncreaseQuantity.setOnClickListener(v -> {
                int currentQuantity = cartItem.getQuantity();
                int stockQuantity = item.getQuantity();

                // Check if we can increase the quantity
                if (currentQuantity < stockQuantity) {
                    cartManager.increaseItemQuantity(item);
                    cartItem.setQuantity(currentQuantity + 1);
                    notifyItemChanged(getAdapterPosition());
                    cartActivity.displayTotalAmount(); // Update total amount in CartActivity
                } else {
                    Toast.makeText(v.getContext(), "Stock limit reached!", Toast.LENGTH_SHORT).show();
                }
            });

            // Handle decreasing the quantity
            buttonDecreaseQuantity.setOnClickListener(v -> {
                if (cartItem.getQuantity() > 1) {
                    cartManager.decreaseItemQuantity(item);
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    cartActivity.displayTotalAmount(); // Update total amount in CartActivity
                    notifyItemChanged(getAdapterPosition());
                } else {
                    cartManager.removeItemFromCart(item);
                    cartItemList.remove(getAdapterPosition());
                    cartActivity.displayTotalAmount(); // Update total amount in CartActivity
                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }
    }
}
