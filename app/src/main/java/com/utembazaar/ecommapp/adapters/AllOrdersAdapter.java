package com.utembazaar.ecommapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utembazaar.ecommapp.R;
import com.utembazaar.ecommapp.UpdateOrderStatusActivity;
import com.utembazaar.ecommapp.models.Item;
import com.utembazaar.ecommapp.models.Order;
import java.util.List;

public class AllOrdersAdapter extends RecyclerView.Adapter<AllOrdersAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public AllOrdersAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderIdTextView.setText("Order ID: " + order.getOrderId());
        holder.totalPriceTextView.setText("Total: RM" + order.getTotalPrice());
        holder.statusTextView.setText("Status: " + order.getStatus());
        holder.AddressTextView.setText("Address: " + order.getAddress());

        List<Item> items = order.getItems();
        StringBuilder itemsStringBuilder = new StringBuilder();
        for (Item item : items) {
            itemsStringBuilder.append(item.getName())  // Assuming Item has a getName() method
                    .append(" (Qty: ")
                    .append(item.getQuantity())  // Assuming Item has a getQuantity() method
                    .append(")\n");  // New line for each item
        }
        holder.ItemsTextView.setText(itemsStringBuilder.toString());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateOrderStatusActivity.class);
            intent.putExtra("ORDER_ID", order.getOrderId());
            intent.putExtra("ORDER_STATUS", order.getStatus());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, totalPriceTextView, statusTextView, AddressTextView, ItemsTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            totalPriceTextView = itemView.findViewById(R.id.totalPriceTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            AddressTextView = itemView.findViewById(R.id.AddressTextView);
            ItemsTextView = itemView.findViewById(R.id.ItemsTextView);
        }
    }
}