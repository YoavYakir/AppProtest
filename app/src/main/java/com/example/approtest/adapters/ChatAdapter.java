package com.example.approtest.adapters;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.approtest.models.ChatMessage;
import com.example.approtest.models.User;
import com.example.approtest.databinding.ItemContainerReceivedMessageBinding;
import com.example.approtest.databinding.ItemContainerSentMessageBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<ChatMessage> chatMessages;
    private final User sender;
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, User sender) {
        this.chatMessages = chatMessages;
        this.sender = sender;
    }


    // Inflate the appropriate layout based on the view type
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEW_TYPE_SENT:
                // For sent messages, inflate the SentMessageViewHolder layout
                return new SentMessageViewHolder(
                        ItemContainerSentMessageBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_RECEIVED:
                // For received messages, inflate the ReceivedMessageViewHolder layout
                return new ReceivedMessageViewHolder(
                        ItemContainerReceivedMessageBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            default:
                Log.d("Error: ", "wrong view type");
                break;
        }
        return null;
    }

    // Bind data to the views based on the view type of the item at the given position
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_SENT:
                ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
                break;
            case VIEW_TYPE_RECEIVED:
                ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position));
                break;
            default:
                Log.d("Error: ", "wrong view type");
                break;
        }

    }

    // Return the number of chat messages in the list
    @Override
    public int getItemCount() {
        return chatMessages.size();
    }


    // Determine the view type of the item at the given position (sent or received message)
    public int getItemViewType(int position) {
        if(chatMessages.get(position).sender.getToken().equals(sender.getToken())){
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    // ViewHolder for Sent Messages
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.sentMessageText.setText(chatMessage.message);
            binding.sentMessageDate.setText(chatMessage.dateTime);
        }
    }
    // ViewHolder for Received Messages
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding){
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        // Method to set data for ReceivedMessageViewHolder
        void setData(ChatMessage chatMessage){
            binding.receivedMessageText.setText(chatMessage.message);
            binding.receivedMessageDate.setText(chatMessage.dateTime);
            binding.receivedMessageUser.setText(chatMessage.sender.getFullName());

        }
    }

}
