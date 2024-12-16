package com.example.Social_Network.Message;

public enum MessageType {
    LIKE("like"),
    CHAT("chat"),
    COMMENT("comment"),
    FOLLOW("follow");

    private String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
