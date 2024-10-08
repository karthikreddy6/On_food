package com.example.onfood;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private CartManager cartManager;

    public CartAdapter(List<CartItem> cartItemList, CartManager cartManager) {
        this.cartItemList = cartItemList;
        this.cartManager = cartManager;
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

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.cartItemName);
            itemQuantity = itemView.findViewById(R.id.cartItemQuantity);
            itemPrice = itemView.findViewById(R.id.cartItemPrice);
            buttonIncreaseQuantity = itemView.findViewById(R.id.increaseQuantity);
            buttonDecreaseQuantity = itemView.findViewById(R.id.decreaseQuantity);
        }

        public void bind(CartItem cartItem) {
            Item item = cartItem.getItem();
            itemName.setText(item.getName() != null ? item.getName() : "Unknown Item");
            itemQuantity.setText(String.valueOf(cartItem.getQuantity()));
            itemPrice.setText("Price: $" + item.getPrice() * cartItem.getQuantity());

            // Handle increasing the quantity
            buttonIncreaseQuantity.setOnClickListener(v -> {
                cartManager.increaseItemQuantity(item);
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                notifyItemChanged(getAdapterPosition());  // Refresh item in RecyclerView
            });

            // Handle decreasing the quantity
            buttonDecreaseQuantity.setOnClickListener(v -> {
                if (cartItem.getQuantity() > 1) {
                    cartManager.decreaseItemQuantity(item);
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    notifyItemChanged(getAdapterPosition());
                } else {
                    cartManager.removeItemFromCart(item);
                    cartItemList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                }
            });
        }
    }
}
