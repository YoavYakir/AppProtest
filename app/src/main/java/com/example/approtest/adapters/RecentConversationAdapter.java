package com.example.approtest.adapters;

import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.approtest.models.ChatMessage;
import com.example.approtest.databinding.ItemContainerEventChatBinding;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.conversationViewHolder>{

    private  List<ChatMessage> chatMessages;

    public RecentConversationAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    // Create a new ViewHolder instance by inflating the layout for each item in the RecyclerView
    @NonNull
    @Override
    public conversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new conversationViewHolder(
                ItemContainerEventChatBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    // Bind data to the ViewHolder at the given position
    @Override
    public void onBindViewHolder(@NonNull conversationViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    // ViewHolder class to hold and manage the individual views for each item in the RecyclerView
    class conversationViewHolder extends RecyclerView.ViewHolder {
        ItemContainerEventChatBinding binding;
        conversationViewHolder(ItemContainerEventChatBinding itemContainerEventChatBinding) {
            super(itemContainerEventChatBinding.getRoot());
            binding = itemContainerEventChatBinding;

        }

        // Method to set data to the views inside the ViewHolder using a ChatMessage object
        void setData(ChatMessage chatMessage) {
            binding.chatImage.setImageBitmap(getconversationImage(chatMessage.event.encodedImage));
            binding.eventNameText.setText(chatMessage.event.eventName);
            binding.eventLastText.setText(chatMessage.message);
        }
    }
    private Bitmap getconversationImage(String encodedImage){
        byte[] bytes = android.util.Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
