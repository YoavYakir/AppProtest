//package com.example.approtest;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.fragment.app.Fragment;
//
//import com.example.approtest.databinding.FragmentChatsBinding;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//
//public class ChatsFragmentCopy extends Fragment implements ChatEventListener {
//
//    private FragmentChatsBinding binding;
//
//    User current;
//    HashMap<String, Event> events;
//
//    public ChatsFragmentCopy(HashMap<String, Event> events, User current) {
//        this.events = events;
//        this.current = current;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = FragmentChatsBinding.inflate(getLayoutInflater());
//        getCurrentEvents();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        return binding.getRoot();
//    }
//
//    private void getCurrentEvents() {
//        loading(true);
//        ArrayList<Event> currentEvents = new ArrayList<Event>();
//        for (Event event : events.values()) {
//            if (event.hasUser(current)) {
//                currentEvents.add(event);
//            }
//        }
//        loading(false);
//        if (currentEvents.size() > 0) {
//            EventsAdapter eventsAdapter = new EventsAdapter(currentEvents, this);
//            binding.eventsRecyclerView.setAdapter(eventsAdapter);
//            binding.eventsRecyclerView.setVisibility(View.VISIBLE);
//        } else {
//            showErrorMessage();
//        }
//    }
//
//
//    private void loading(boolean isLoading) {
//        if (isLoading){
//            binding.chatsProgressBar.setVisibility(View.VISIBLE);
//        } else {
//            binding.chatsProgressBar.setVisibility(View.INVISIBLE);
//        }
//    }
//
//
//    private void showErrorMessage(){
//        binding.textErrorMessage.setText(String.format("%s", "Events are unavailiable"));
//        binding.textErrorMessage.setVisibility(View.VISIBLE);
//
//    }
//
//
//    @Override
//    public void onChatEventClicked(Event event) {
//        Intent intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
//        intent.putExtra(Constants.KEY_EVENT, event);
//        intent.putExtra(Constants.KEY_CURRENT_USER, current);
//        startActivity(intent);
//    }
//}