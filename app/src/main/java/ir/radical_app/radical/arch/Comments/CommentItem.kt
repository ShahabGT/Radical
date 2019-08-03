package ir.radical_app.radical.arch.Comments


import com.google.gson.annotations.SerializedName

data class CommentItem(
    val comment: String,
    val date: String,
    @SerializedName("shop_id")
    val shopId: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("comment_id")
    val commentId: String,
    val name: String
)