package com.example.onfood;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private OnItemClickListener onItemClickListener;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_layout, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        if (orderList == null || position >= orderList.size()) return;

        Order order = orderList.get(position);
        holder.orderIdTextView.setText("Order ID: " + order.getOrderId());
        holder.totalAmountTextView.setText("₹" + order.getAmount());
        holder.orderDateTextView.setText("Order on: " + order.getOrderDate());
        holder.orderTimeTextView.setText(order.getOrderTime());

        // Prevent NullPointerException by checking for null before using the status
        String status = order.getStatus();
        if (status == null) {
            status = "Unknown"; // Default value if status is null
        }

        // Update status indicator color
        int statusColor;
        switch (status) {
            case "confirmed":
                statusColor = R.color.status_pending;
                break;
            case "delivered":

                statusColor = R.color.status_delivered;
                break;
            case "Cancelled":
                statusColor = R.color.status_cancelled;
                break;
            default:
                statusColor = R.color.status_unknown;
                break;
        }

        holder.statusIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), statusColor));

        // Set click listener for each order item
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return (orderList != null) ? orderList.size() : 0;
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, totalAmountTextView, orderDateTextView, orderTimeTextView;
        View statusIndicator;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            totalAmountTextView = itemView.findViewById(R.id.totalAmountTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            orderTimeTextView = itemView.findViewById(R.id.orderTimeTextView);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
        }
    }

    // Define the interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Method to set the item click listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
