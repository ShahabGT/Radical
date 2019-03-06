package ir.radical_app.radical.models;

import com.google.gson.annotations.SerializedName;

public class JsonResponse {

    private String message;

    private String name;
    @SerializedName("category_id")
    private String categoryId;
    private String sex;
    private String email;
    private String planid;
    private String buydate;
    private String expdate;
    private String nowdate;
    private String amount;
    private String invitecode;
    private String count;
    private String sum;
    private String version;
    private String urgent;
    private String education;
    private String color;
    private String job;
    private String region;
    private String description;

    public String getDescription() {
        return description;
    }

    public String getEducation() {
        return education;
    }

    public String getColor() {
        return color;
    }

    public String getJob() {
        return job;
    }

    public String getRegion() {
        return region;
    }

    public String getVersion() {
        return version;
    }

    public String getUrgent() {
        return urgent;
    }

    public String getInvitecode() {
        return invitecode;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getAmount() {
        return amount;
    }

    public String getNowdate() {
        return nowdate;
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

    public String getSex() {
        return sex;
    }

    public String getEmail() {
        return email;
    }

    public String getPlanid() {
        return planid;
    }

    public String getBuydate() {
        return buydate;
    }

    public String getExpdate() {
        return expdate;
    }

    public String getCount() {
        return count;
    }

    public String getSum() {
        return sum;
    }
}
