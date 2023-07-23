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

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder>{

    private final List<Event> events;

    private final ChatEventListener chatEventListener;

    public EventsAdapter(List<Event> events, ChatEventListener chatEventListener) {
        this.events = events;
        this.chatEventListener = chatEventListener;
    }


    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerEventChatBinding itemContainerEventChatBinding = ItemContainerEventChatBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new EventViewHolder(itemContainerEventChatBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.setEventData(events.get(position));

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {

        ItemContainerEventChatBinding binding;

        EventViewHolder(ItemContainerEventChatBinding itemContainerGroupBinding){
            super(itemContainerGroupBinding.getRoot());
            binding = itemContainerGroupBinding;
        }

        void setEventData(Event event){
            binding.eventNameText.setText(event.eventName);
            binding.chatImage.setImageBitmap(getEventImage(event.encodedImage));
            binding.getRoot().setOnClickListener(v -> chatEventListener.onChatEventClicked(event));
        }


    }

    private Bitmap getEventImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
