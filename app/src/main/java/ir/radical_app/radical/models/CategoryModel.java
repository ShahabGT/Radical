package ir.radical_app.radical.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CategoryModel {

    @SerializedName("data")
    private ArrayList<JsonResponse> list;

    @SerializedName("slider")
    private ArrayList<SliderResponse> slider;


    public ArrayList<SliderResponse> getSlider() {
        return slider;
    }

    public void setSlider(ArrayList<SliderResponse> slider) {
        this.slider = slider;
    }

    public ArrayList<JsonResponse> getList() {
        return list;
    }

    public void setList(ArrayList<JsonResponse> list) {
        this.list = list;
    }
}
