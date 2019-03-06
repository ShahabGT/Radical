package ir.radical_app.radical.models;

import java.util.ArrayList;

public class ChatModel {


    private ArrayList<MessageModel> data;
    private String message;

    public ArrayList<MessageModel> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
