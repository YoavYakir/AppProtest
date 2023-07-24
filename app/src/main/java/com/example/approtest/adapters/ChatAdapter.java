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

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEW_TYPE_SENT:
                return new SentMessageViewHolder(
                        ItemContainerSentMessageBinding.inflate(
                                LayoutInflater.from(parent.getContext()),
                                parent,
                                false
                        )
                );
            case VIEW_TYPE_RECEIVED:
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

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public int getItemViewType(int position) {
        if(chatMessages.get(position).sender.getToken().equals(sender.getToken())){
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }


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

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding){
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }


        void setData(ChatMessage chatMessage){
            binding.receivedMessageText.setText(chatMessage.message);
            binding.receivedMessageDate.setText(chatMessage.dateTime);
            binding.receivedMessageUser.setText(chatMessage.sender.getFullName());

        }
    }

}
