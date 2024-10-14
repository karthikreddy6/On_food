package com.example.onfood;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<String> categories;
    private OnCategoryClickListener categoryClickListener;
    private int selectedPosition = -1; // Track the selected position

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public CategoryAdapter(List<String> categories, OnCategoryClickListener categoryClickListener) {
        this.categories = categories;
        this.categoryClickListener = categoryClickListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.bind(category, position);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            itemView.setOnClickListener(v -> {
                selectedPosition = getAdapterPosition(); // Update the selected position
                if (categoryClickListener != null) {
                    categoryClickListener.onCategoryClick(categories.get(selectedPosition));
                }
                notifyDataSetChanged(); // Notify adapter to refresh views
            });
        }

        public void bind(String category, int position) {
            categoryName.setText(category);
            // Change background based on selection
            if (selectedPosition == position) {
                itemView.setBackgroundResource(R.drawable.gradient_background_on);
            } else {
                itemView.setBackgroundResource(R.drawable.gradient_background_of);
            }
        }
    }
}