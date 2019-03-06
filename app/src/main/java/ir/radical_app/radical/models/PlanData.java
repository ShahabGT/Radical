package ir.radical_app.radical.models;

import com.google.gson.annotations.SerializedName;

public class PlanData {
    private String description;
    @SerializedName("plan_bronze")
    private String planBronze;
    @SerializedName("plan_diamond")
    private String planDiamond;
    private String plan;

    public String getPlan() {
        return plan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlanBronze() {
        return planBronze;
    }

    public void setPlanBronze(String planBronze) {
        this.planBronze = planBronze;
    }

    public String getPlanDiamond() {
        return planDiamond;
    }

    public void setPlanDiamond(String planDiamond) {
        this.planDiamond = planDiamond;
    }
}
