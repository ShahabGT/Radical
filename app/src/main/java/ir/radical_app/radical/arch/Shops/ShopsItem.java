package ir.radical_app.radical.arch.Shops;

import com.google.gson.annotations.SerializedName;

public class ShopsItem{

    @SerializedName("shop_id")
    private String shopId;

    @SerializedName("category_id")
    private String categoryId;

    @SerializedName("category_name")
    private String categoryName;

    private String name;

    @SerializedName("max_discount")
    private String maxDiscount;

    private String region;
    private String count;
    private String sum;
    private String lat;
    private String lon;

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public String getCount() {
        return count;
    }

    public String getSum() {
        return sum;
    }

    public String getRegion() {
        return region;
    }

    public String getShopId() {
        return shopId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaxDiscount() {
        return maxDiscount;
    }

}