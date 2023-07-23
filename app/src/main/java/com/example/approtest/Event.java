package com.example.approtest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//import com.google.


public class Event implements Serializable {
    protected String eventName;
    protected HashMap<String,User> participants;
    protected String date;
//    protected ArrayList<Message> chat;

    protected double latitude;

    protected double longitude;

    protected String encodedImage;



    public Event (String eventName, String date, double latitude, double longitude, HashMap<String,User> participants, ArrayList<ChatMessage> chatMessages){
        this(eventName,date, latitude, longitude);
        for(Map.Entry<String,User> entry : participants.entrySet())
        {
           this.participants.put(entry.getKey(),entry.getValue()) ;
        }
//        for(int i=0 ;i  < chat.size();i++)
//        {
//            this.chat.add(chat.get(i));
//        }

    }

    public Event (String eventName, String date, double latitude, double longitude){
        this.eventName = String.valueOf(eventName);
        this.date = String.valueOf(date);
        this.latitude = latitude;
        this.longitude = longitude;
        this.participants = new HashMap<String,User>();
//        this.chat = new ArrayList<ChatMessage>();
    }


    public Event(Event event)
    {
        this.eventName = event.getEventName();
        this.date = event.getDate();
        this.latitude = event.getLatitude();;
        this.longitude = event.getLongitude();
        this.participants = event.getParticipants();
//        this.chat = event.getChat();
    }
    public Event()
    {
        this.participants = new HashMap<String,User>();
//        this.chat = new ArrayList<ChatMessage>();
    }

    public String getEventName(){
        return eventName;
    }

    public double getLatitude(){return latitude;}

    public double getLongitude(){return longitude;}

    public String getDate() {
        return date;
    }

    public HashMap<String, User> getParticipants() {
        HashMap<String,User> users = new HashMap<String,User>();
        for(int i = 0; i < participants.size();i++)
        {
            for(Map.Entry<String,User> entry : participants.entrySet())
            {
                users.put(entry.getKey(),entry.getValue()) ;
            }
        }
        return users;
    }

//    public ArrayList<ChatMessage> getChat() {
//        ArrayList<ChatMessage> chat = new ArrayList<ChatMessage>();
//        for(int i = 0; i < this.chat.size();i++)
//        {
//            chat.add(new ChatMessage(this.chat.get(i)));
//        }
//        return chat;
//    }

    public void addUser(User user) {this.participants.put(user.getToken(),new User(user)); }

    public boolean hasUser(User user)
    {
        return participants.containsKey(user.getToken());
    }
}
