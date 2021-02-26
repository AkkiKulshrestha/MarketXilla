package in.techxilla.www.marketxilla.model;

public class SmartPlanModel {

    private String planname;
    private String planDescription;
    private int image;
    private int color;

    public SmartPlanModel(final String planname, final String planDescription, final int image, final int color) {
        this.planname = planname;
        this.planDescription = planDescription;
        this.image = image;
        this.color = color;
    }

    public String getPlanname() {
        return planname;
    }

    public void setPlanname(String planname) {
        this.planname = planname;
    }

    public String getPlanDescription() {
        return planDescription;
    }

    public void setPlanDescription(String planDescription) {
        this.planDescription = planDescription;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}

