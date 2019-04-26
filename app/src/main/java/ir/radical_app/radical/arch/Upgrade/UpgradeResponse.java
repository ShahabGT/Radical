package ir.radical_app.radical.arch.Upgrade;

import java.util.ArrayList;

public class UpgradeResponse {


    private String message;
    private ArrayList<UpgradeItem> data;


    public String getMessage() {
        return message;
    }

    public ArrayList<UpgradeItem> getData() {
        return data;
    }
}
