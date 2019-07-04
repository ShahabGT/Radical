package ir.radical_app.radical.models

import com.google.gson.annotations.SerializedName

data class BuyDetailsItem(val price:String,
                          @SerializedName("price_discount") val priceDiscount:String,
                          @SerializedName("discount_percent") val discountPercent :String,
                          @SerializedName("discount_title") val discountTitle :String)
