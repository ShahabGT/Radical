package ir.radical_app.radical.arch.BuyHistory;

import com.google.gson.annotations.SerializedName;

public class BuyItem {

    @SerializedName("shop_buy_id")
    private String shopBuyId;

    @SerializedName("shop_id")
    private String shopId;

    private String name;

    @SerializedName("plan_id")
    private String planId;

    @SerializedName("total_amount")
    private String totalAmount;

    @SerializedName("total_discount")
    private String totalDiscount;

    @SerializedName("total_pay")
    private String totalPay;

    private String date;


    public String getShopBuyId() {
        return shopBuyId;
    }

    public String getShopId() {
        return shopId;
    }

    public String getName() {
        return name;
    }

    public String getPlanId() {
        return planId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getTotalDiscount() {
        return totalDiscount;
    }

    public String getTotalPay() {
        return totalPay;
    }

    public String getDate() {
        return date;
    }
}
