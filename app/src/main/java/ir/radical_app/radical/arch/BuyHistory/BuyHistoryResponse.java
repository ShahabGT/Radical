package ir.radical_app.radical.arch.BuyHistory;

import java.util.ArrayList;

public class BuyHistoryResponse {


    private String message;
    private ArrayList<BuyItem> data;


    public String getMessage() {
        return message;
    }

    public ArrayList<BuyItem> getData() {
        return data;
    }
}
