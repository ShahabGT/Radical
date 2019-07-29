package ir.radical_app.radical.models

import com.google.gson.annotations.SerializedName


data class ShopData (val message:String,
                     val name:String,
                     @SerializedName("category_name") val categoryName:String,
                     @SerializedName("category_id") val categoryId:String,
                     @SerializedName("shop_id") val shopId:String,
                     val address:String,
                     val description:String,
                     val open:String,
                     val tel:String,
                     val tel2:String,
                     val instagram:String,
                     val telegram:String,
                     val website:String,
                     @SerializedName("pic_num") val picNum:Int,
                     val bookmark:String,
                     val plan:String,
                     val count:String,
                     val likes:Int,
                     val dislikes:Int,
                     val user:Int,
                     val sum:String,
                     val lat:String,
                     val lon:String
                     )




