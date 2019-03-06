package ir.radical_app.radical.models;

import com.google.gson.annotations.SerializedName;

public class BuyDetailsItem {



    private String price;
    @SerializedName("price_discount")
    private String priceDiscount;

    @SerializedName("discount_percent")
    private String discountPercent;

    @SerializedName("discount_title")
    private String discountTitle;


    public String getPrice() {
        return price;
    }

    public String getPriceDiscount() {
        return priceDiscount;
    }

    public String getDiscountPercent() {
        return discountPercent;
    }

    public String getDiscountTitle() {
        return discountTitle;
    }
}
