package ir.radical_app.radical.models;

import com.google.gson.annotations.SerializedName;

public class SliderResponse {

    @SerializedName("slider_id")
    private int sliderId;
    private String pic;
    private String description;
    private String link;


    public int getSliderId() {
        return sliderId;
    }

    public String getPic() {
        return pic;
    }

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }
}
