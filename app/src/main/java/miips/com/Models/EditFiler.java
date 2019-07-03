package miips.com.Models;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class EditFiler {
    private Context mContext;
    private static final String TAG = EditFiler.class.getName();

    public EditFiler(Context context) {
        mContext = context;
    }

    //set filer to EditTex miipsname, valid only a-z, "_" , "-", "."
    public void setFilter(final EditText namiips, EditText mUsername) {

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start,
                                       int end, Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {
                    Log.d(TAG, "i ta assim: "+ i);
                    if (!Character.isLetterOrDigit(source.charAt(i)) &&
                            !Character.toString(source.charAt(i)).equals("_") &&
                            !Character.toString(source.charAt(i)).equals("-") &&
                            !Character.toString(source.charAt(i)).equals(".")){
                        Toast.makeText(mContext, "Caracter não permitido", Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        };

        //set filer to a editTex name, valid only a-z, A-Z

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

        namiips.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable arg) {

                String s = arg.toString();
                if(!s.equals(s.toLowerCase())){
                    s = s.toLowerCase();
                    namiips.setText(s);
                    namiips.setSelection(namiips.getText().length());

                }

            }
        });

        namiips.setFilters(new InputFilter[]{filter});
        mUsername.setFilters(new InputFilter[]{filterTwo});

    }
}