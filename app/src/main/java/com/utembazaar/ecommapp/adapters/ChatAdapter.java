package com.utembazaar.ecommapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.utembazaar.ecommapp.R;
import com.utembazaar.ecommapp.models.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<Message> messages = new ArrayList<>();

    public void addMessage(Message message) {
        Log.d("ChatAdapter", "Adding message: " + message.getContent());
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }



    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView senderTextView;
        private TextView contentTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
        }

        public void bind(Message message) {
            senderTextView.setText(message.getSender());
            contentTextView.setText(message.getContent());
        }
    }
}