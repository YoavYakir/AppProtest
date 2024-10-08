package com.example.approtest.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.approtest.utilities.ChatEventListener;
import com.example.approtest.utilities.Constants;
import com.example.approtest.activities.ChatActivity;
import com.example.approtest.adapters.EventsAdapter;
import com.example.approtest.adapters.RecentConversationAdapter;
import com.example.approtest.databinding.FragmentChatsBinding;
import com.example.approtest.models.ChatMessage;
import com.example.approtest.models.Event;
import com.example.approtest.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;


public class ChatsFragment extends Fragment implements ChatEventListener {

    private FragmentChatsBinding binding;

    User current;
    HashMap<String, Event> events;

    private List<ChatMessage> conversations;
    private RecentConversationAdapter conversationAdapter;

    private FirebaseFirestore database;

    // Constructor to pass data to the fragment
    public ChatsFragment(HashMap<String, Event> events, User current) {
        this.events = events;
        this.current = current;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentChatsBinding.inflate(getLayoutInflater());
        init();
        getCurrentEvents();
    }

    public void init(){
        conversations= new ArrayList<>();
        conversationAdapter = new RecentConversationAdapter(conversations);
        binding.eventsRecyclerView.setAdapter(conversationAdapter);
        database = FirebaseFirestore.getInstance();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return binding.getRoot();
    }
    // Fetch the current events that the user is part of and display them
    private void getCurrentEvents() {
        loading(true);
        ArrayList<Event> currentEvents = new ArrayList<Event>();
        for (Event event : events.values()) {
            if (event.hasUser(current)) {
                currentEvents.add(event);
            }
        }
        loading(false);
        if (currentEvents.size() > 0) {
            EventsAdapter eventsAdapter = new EventsAdapter(currentEvents, this);
            binding.eventsRecyclerView.setAdapter(eventsAdapter);
            binding.eventsRecyclerView.setVisibility(View.VISIBLE);
        } else {
            showErrorMessage();
        }
    }

    // Show or hide the loading progress bar
    private void loading(boolean isLoading) {
        if (isLoading){
            binding.chatsProgressBar.setVisibility(View.VISIBLE);
        } else {
            binding.chatsProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    // Show an error message when no events are available
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s", "Events are unavailiable"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);

    }

    // Handle click event for a specific chat event, navigate to ChatActivity
    @Override
    public void onChatEventClicked(Event event) {
        Intent intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_EVENT, event);
        intent.putExtra(Constants.KEY_CURRENT_USER, current);
        startActivity(intent);
    }
}