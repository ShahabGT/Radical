package ir.radical_app.radical.models;

import com.google.gson.annotations.SerializedName;

public class ShopData {
    private String message;
    private String name;
    @SerializedName("category_name")
    private String categoryName;
    @SerializedName("shop_id")
    private String shopId;
    private String address;
    private String description;
    private String open;
    private String tel;
    private String tel2;
    private String instagram;
    private String telegram;
    private String website;
    @SerializedName("pic_num")
    private int picNum;
    private String bookmark;
    private String plan;
    private String count;
    private String sum;


    public String getPlan() {
        return plan;
    }

    public String getBookmark() {
        return bookmark;
    }

    public void setBookmark(String bookmark) {
        this.bookmark = bookmark;
    }

    public String getShopId() {
        return shopId;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public String getOpen() {
        return open;
    }

    public String getTel() {
        return tel;
    }

    public String getTel2() {
        return tel2;
    }

    public String getInstagram() {
        return instagram;
    }

    public String getTelegram() {
        return telegram;
    }

    public String getWebsite() {
        return website;
    }

    public int getPicNum() {
        return picNum;
    }

    public String getCount() {
        return count;
    }

    public String getSum() {
        return sum;
    }
}
