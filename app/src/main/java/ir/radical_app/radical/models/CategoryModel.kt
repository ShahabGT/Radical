package ir.radical_app.radical.models

import com.google.gson.annotations.SerializedName

data class CategoryModel(@SerializedName("data") val list:List<JsonResponse>,
                         val slider:List<SliderResponse>)
