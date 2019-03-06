package ir.radical_app.radical.models;

import java.util.ArrayList;

public class ShopDetailsModel {


    private ShopData data;
    private ArrayList<PlanData> plans;

    public ShopData getData() {
        return data;
    }

    public void setData(ShopData data) {
        this.data = data;
    }

    public ArrayList<PlanData> getPlans() {
        return plans;
    }

}

