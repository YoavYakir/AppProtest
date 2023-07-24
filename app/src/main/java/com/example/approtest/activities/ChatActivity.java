package com.example.approtest.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;

import com.example.approtest.models.ChatMessage;
import com.example.approtest.utilities.Constants;
import com.example.approtest.models.Event;
import com.example.approtest.models.User;
import com.example.approtest.adapters.ChatAdapter;
import com.example.approtest.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private Event event;

    private User current;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore database;
    private String conversationId = null;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadChatEventDetails();
        init();
        listenMessages();
    }

    private void init() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                this.current
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void listenMessages(){
        database.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_EVENT_NAME, event.eventName)
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null){
            return;
        } else if (value != null) {
            int count = chatMessages.size();
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    chatMessages.add(documentChange.getDocument().toObject(ChatMessage.class));
                    ChatMessage lastChatMessage = chatMessages.get(chatMessages.size() - 1);
                    lastChatMessage.dateTime = getReadableDateTime(lastChatMessage.dateObject);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0){
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeChanged(chatMessages.size(), chatMessages.size());
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
        if(conversationId == null){
            checkForConversationsRemotely();
        }
    };

    private void loadChatEventDetails(){
        event = (Event) getIntent().getSerializableExtra(Constants.KEY_EVENT);
        current = (User) getIntent().getSerializableExtra(Constants.KEY_CURRENT_USER);
        binding.chatTitle.setText(event.eventName);
    }

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER, current);
        message.put(Constants.KEY_EVENT, event);
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        message.put(Constants.KEY_DATE_OBJECT, new Date());
        message.put(Constants.KEY_EVENT_NAME, event.eventName);
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if(conversationId != null) {
            updateConversation(binding.inputMessage.getText().toString());
        } else {
          HashMap<String, Object> conversation = new HashMap<>();
          conversation.put(Constants.KEY_EVENT, event);
          conversation.put(Constants.KEY_EVENT_NAME, event.eventName);
          conversation.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
          conversation.put(Constants.KEY_DATE_OBJECT, new Date());
          addConversation(conversation);
        }
        binding.inputMessage.setText(null);


    }

    private void setListeners(){
        binding.chatImageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setOnClickListener(v -> sendMessage());
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversation(HashMap<String, Object> conversation){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversation)
                .addOnSuccessListener(documentReference -> conversationId = documentReference.getId());
    }

    private  void updateConversation(String message){
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversationId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE, message,
                Constants.KEY_DATE_OBJECT, new Date()
        );
    }

    private void checkForConversationsRemotely(){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_EVENT_NAME, this.event.eventName)
                .get()
                .addOnCompleteListener(conversationOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversationOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversationId = documentSnapshot.getId();
        }
    };
}