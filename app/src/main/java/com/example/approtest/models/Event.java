package com.example.approtest.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//import com.google.


public class Event implements Serializable {
    public String eventName;
    public HashMap<String, User> participants;
    public String date;
//    protected ArrayList<Message> chat;

    public double latitude;

    public double longitude;

    public String encodedImage;

    public Event (String eventName, String date, double latitude, double longitude, HashMap<String,User> participants, ArrayList<ChatMessage> chatMessages, String encodedImage){
        this(eventName,date, latitude, longitude,encodedImage);
        for(Map.Entry<String,User> entry : participants.entrySet())
        {
            this.participants.put(entry.getKey(),entry.getValue()) ;
        }
//        for(int i=0 ;i  < chat.size();i++)
//        {
//            this.chat.add(chat.get(i));
//        }

    }

    public Event (String eventName, String date, double latitude, double longitude,String encodedImage){
        this.eventName = String.valueOf(eventName);
        this.date = String.valueOf(date);
        this.latitude = latitude;
        this.longitude = longitude;
        this.participants = new HashMap<String,User>();
        this.encodedImage = String.valueOf(encodedImage);
//        this.chat = new ArrayList<ChatMessage>();
    }


    public Event(Event event)
    {
        this.eventName = event.getEventName();
        this.date = event.getDate();
        this.latitude = event.getLatitude();;
        this.longitude = event.getLongitude();
        this.participants = event.getParticipants();
        this.encodedImage = getEncodedImage();
    }
    public Event()
    {
        this.participants = new HashMap<String,User>();
//        this.chat = new ArrayList<ChatMessage>();
    }

    public void setEncodedImage(String encodedImage)
    {
        this.encodedImage = String.valueOf(encodedImage);
    }

    public String getEventName(){
        return eventName;
    }

    public double getLatitude(){return latitude;}

    public double getLongitude(){return longitude;}

    public String getDate() {
        return date;
    }

    public String getEncodedImage(){return String.valueOf(encodedImage);}

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