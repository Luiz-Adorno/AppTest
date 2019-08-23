package miips.com.Models.HomeModels;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class VerticalModel {
    Drawable colorTitle;
    String title;
    ArrayList<HorizontalModel> arrayList;

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
