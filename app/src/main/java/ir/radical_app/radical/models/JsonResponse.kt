package ir.radical_app.radical.models

import com.google.gson.annotations.SerializedName


data class JsonResponse (val message:String="",
                         val name:String="",
                         val birthday:String="",
                         val family:String="",
                         @SerializedName("category_id") val categoryId:String="",
                         val sex:String="",
                         val email:String="",
                         val planid:String="",
                         val buydate:String="",
                         val remaining:String="",
                         val expdate:String="",
                         val nowdate:String="",
                         val amount3:String="",
                         val amount7:String="",
                         val amount15:String="",
                         val amount:String="",
                         val amount60:String="",
                         val amount90:String="",
                         val invitecode:String="",
                         val count:String="",
                         @SerializedName("social_count") val socialCount:String="",
                         @SerializedName("invite_count") val inviteCount:String="",
                         val sum:String="",
                         val version:String="",
                         val urgent:String="",
                         val education:String="",
                         val color:String="",
                         val job:String="",
                         val region:String="",
                         val description:String="",
                         val merchant:String=""
                         )


