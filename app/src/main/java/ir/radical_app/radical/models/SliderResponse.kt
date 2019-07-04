package ir.radical_app.radical.models
import com.google.gson.annotations.SerializedName

data class SliderResponse (@SerializedName("slider_id") val sliderId:Int,
                           val pic:String,
                           val description:String,
                           val link:String
                           )


