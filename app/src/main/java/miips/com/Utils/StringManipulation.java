package miips.com.Utils;

import android.util.Log;

public class StringManipulation {


    private static final String TAG = StringManipulation.class.getName();

    public static String expandUsername(String username) {
        return username.replace(".", " ");
    }

    public static String condenseUsername(String username) {
        if (username == null) {
            return username;
        }
        return username.replace(" ", ".");
    }

    public static String condenseUri(String uri) {
        String[] uriSplit = uri.split("/");
        Log.d(TAG, "condenseUri: 3 ta assim: " + uriSplit[4]);
        return uriSplit[4];
    }
}
