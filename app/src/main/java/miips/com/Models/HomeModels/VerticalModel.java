package miips.com.Models.HomeModels;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class VerticalModel {
    Drawable colorTitle;
    int colorString;
    String title;
    ArrayList<HorizontalModel> arrayList;

    public int getColorString() {
        return colorString;
    }

    public void setColorString(int colorString) {
        this.colorString = colorString;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<HorizontalModel> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<HorizontalModel> arrayList) {
        this.arrayList = arrayList;
    }

    public Drawable getColorTitle() {
        return colorTitle;
    }

    public void setColorTitle(Drawable colorTitle) {
        this.colorTitle = colorTitle;
    }

}
