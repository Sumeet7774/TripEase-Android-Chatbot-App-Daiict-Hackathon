package com.example.tripease;

public class ChatMessageModel {
    private String sender;  // Either "user" or "bot"
    private String message;

    public ChatMessageModel(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }
}
