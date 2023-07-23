package com.example.approtest;

import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.approtest.databinding.ItemContainerEventChatBinding;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.conversationViewHolder>{

    private  List<ChatMessage> chatMessages;

    public RecentConversationAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }


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

    @Override
    public void onBindViewHolder(@NonNull conversationViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class conversationViewHolder extends RecyclerView.ViewHolder {
        ItemContainerEventChatBinding binding;
        conversationViewHolder(ItemContainerEventChatBinding itemContainerEventChatBinding) {
            super(itemContainerEventChatBinding.getRoot());
            binding = itemContainerEventChatBinding;
        }

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
