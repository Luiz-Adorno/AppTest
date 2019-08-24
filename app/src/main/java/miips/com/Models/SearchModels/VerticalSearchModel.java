package miips.com.Models.SearchModels;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

import miips.com.Models.HomeModels.HorizontalModel;

public class VerticalSearchModel {
    Drawable colorTitle;
    String title;
    ArrayList<HorizontalSearchModel> arrayList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<HorizontalSearchModel> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<HorizontalSearchModel> arrayList) {
        this.arrayList = arrayList;
    }

    public Drawable getColorTitle() {
        return colorTitle;
    }

    public void setColorTitle(Drawable colorTitle) {
        this.colorTitle = colorTitle;
    }

}
