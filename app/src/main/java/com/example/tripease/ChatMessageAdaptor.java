package com.example.tripease;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.List;

public class ChatMessageAdaptor extends RecyclerView.Adapter<ChatMessageAdaptor.ViewHolder> {
    private List<ChatMessageModel> chatMessages;

    public ChatMessageAdaptor(List<ChatMessageModel> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageModel message = chatMessages.get(position);
        if (message.getSender().equals("user")) {
            return 0; // User message
        } else {
            return 1; // Bot message
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_message_item, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bot_message_item, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessageModel message = chatMessages.get(position);
        holder.messageTextView.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void addMessage(ChatMessageModel message) {
        chatMessages.add(message);
        notifyItemInserted(chatMessages.size() - 1); // Notify adapter that a new item has been added
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
        }
    }
}
