package ir.radical_app.radical.models;

import com.google.gson.annotations.SerializedName;

public class MessageModel {

    private String message;
    private String title;
    @SerializedName("sender_user_id")
    private String sender;
    @SerializedName("receiver_user_id")
    private String receiver;

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    private String date;

    public String getReceiver() {
        return receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
