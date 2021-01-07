package in.techxilla.www.marketxilla.model;

public class SmartPlanModel {

    String planname;
    String planDescription;
    int image;



    int color;


    public SmartPlanModel(String planname, String planDescription, int image, int color) {
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

