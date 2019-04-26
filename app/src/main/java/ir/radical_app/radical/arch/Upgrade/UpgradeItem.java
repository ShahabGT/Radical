package ir.radical_app.radical.arch.Upgrade;

import com.google.gson.annotations.SerializedName;

public class UpgradeItem {


    private String amount;

    @SerializedName("merchant_id")
    private String merchantId;

    @SerializedName("wallet_price")
    private String walletPrice;

    @SerializedName("giftcode")
    private String giftCode;

    private String rescode;

    private String description;

    private String date;

    public String getMerchantId() {
        return merchantId;
    }

    public String getAmount() {
        return amount;
    }

    public String getWalletPrice() {
        return walletPrice;
    }

    public String getGiftCode() {
        return giftCode;
    }

    public String getRescode() {
        return rescode;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }
}
