package miips.com.Utils;

import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {
    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx) {
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.setTextVisibility(false);
        bottomNavigationViewEx.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        bottomNavigationViewEx.setItemHorizontalTranslationEnabled(false);

    }

}
