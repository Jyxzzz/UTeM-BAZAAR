package com.utembazaar.ecommapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utembazaar.ecommapp.R;
import com.utembazaar.ecommapp.models.Order;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder> {

    private List<Order> orderList;
    private Context context;

    public OrdersAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderIdTextView.setText("Order ID: " + order.getOrderId());
        holder.orderStatusTextView.setText("Status: " + order.getStatus());
        holder.totalPriceTextView.setText("Total Price: RM" + String.format("%.2f", order.getTotalPrice()));
        holder.orderDateTextView.setText("Order Date: " + order.getFormattedTimestamp()); // Assuming you format the timestamp in the model
        holder.Address.setText("Address:" + order.getAddress());
//        holder.itemView.setOnClickListener(v -> {
//            Intent intent = new Intent(context, UpdateOrderStatusActivity.class);
//            intent.putExtra("ORDER_ID", order.getOrderId());
//            intent.putExtra("ORDER_STATUS", order.getStatus());
//            context.startActivity(intent);
//        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {

        TextView orderIdTextView, orderStatusTextView, totalPriceTextView, orderDateTextView, Address;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            orderStatusTextView = itemView.findViewById(R.id.orderStatusTextView);
            totalPriceTextView = itemView.findViewById(R.id.totalPriceTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            Address = itemView.findViewById(R.id.Address);
        }
    }
}
