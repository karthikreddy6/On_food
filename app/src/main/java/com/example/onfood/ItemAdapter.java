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
import java.lang.ref.WeakReference;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> implements CartChangeListener {
    private static final String TAG = "ItemAdapter"; // Standardized Log Tag
    private List<Item> itemList;
    private CartManager cartManager;
    private WeakReference<OnAddToCartListener> addToCartListenerRef;

    public interface OnAddToCartListener {
        void onAddToCart(Item item);
    }

    public ItemAdapter(List<Item> itemList, CartManager cartManager, OnAddToCartListener addToCartListener) {
        this.itemList = itemList;
        this.cartManager = cartManager;
        this.addToCartListenerRef = new WeakReference<>(addToCartListener);
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
        holder.bind(item, position);
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

        public void bind(Item item, int position) {
            itemName.setText(item.getName());
            itemPrice.setText("Price: " + item.getPrice());
            itemDescription.setText(item.getDescription());

            updateQuantityDisplay(item);

            Glide.with(itemImage.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.image)  // Set a placeholder
                    .error(R.drawable.ic_cart)      // Set an error image
                    .into(itemImage);

            buttonAddToCart.setOnClickListener(v -> {
                buttonAddToCart.setEnabled(false);
                cartManager.addToCart(item);

                OnAddToCartListener listener = addToCartListenerRef.get();
                if (listener != null) {
                    listener.onAddToCart(item);
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

                // Disable the button if quantity in cart equals stock quantity
                if (quantity >= stockQuantity) {
                    buttonAddToCart.setEnabled(false);
                    Toast.makeText(buttonAddToCart.getContext(), "Out of Stock", Toast.LENGTH_SHORT).show();
                    buttonAddToCart.setAlpha(0.5f); // Optional: visually indicate it's disabled
                } else {
                    buttonAddToCart.setEnabled(true);
                    buttonAddToCart.setAlpha(1.0f); // Restore opacity when enabled
                }
            } else {
                itemQuantity.setText("Out of Stock");
                buttonAddToCart.setVisibility(View.GONE);
            }

            Log.d(TAG, item.getName() + " quantity: " + stockQuantity);
        }

    }

    @Override
    public void onCartChanged() {
        notifyDataSetChanged(); // Consider optimizing this for performance
    }
}
