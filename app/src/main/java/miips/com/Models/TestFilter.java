package miips.com.Models;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.Toast;

public class TestFilter {
    private Context mContext;

    public TestFilter(Context context) {
        mContext = context;
    }

    //set filer to a editTex name and miipsname, valid only a-z, "_" , "-", "."
    public void setFilter(EditText namiips, EditText mUsername) {
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i)) &&
                            !Character.toString(source.charAt(i)).equals("_") &&
                            !Character.toString(source.charAt(i)).equals("-") &&
                            !Character.toString(source.charAt(i)).equals(".")) {
                       Toast.makeText(mContext, "Caracter não permetido", Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        };

        InputFilter filterTwo = new InputFilter() {
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {
                    if (!Character.isLetter(source.charAt(i)) &&
                            !Character.toString(source.charAt(i)).equals(" ")
                            ) {
                        Toast.makeText(mContext, "Caracter não permetido", Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        };

        namiips.setFilters(new InputFilter[]{filter});
        mUsername.setFilters(new InputFilter[]{filterTwo});
    }
}