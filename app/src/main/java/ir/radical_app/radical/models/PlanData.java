package ir.radical_app.radical.models;

import com.google.gson.annotations.SerializedName;

public class PlanData {
    private String description;
    private String discount;
    private String plan;

    public String getPlan() {
        return plan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }


}
