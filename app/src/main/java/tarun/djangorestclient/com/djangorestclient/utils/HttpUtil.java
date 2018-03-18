package tarun.djangorestclient.com.djangorestclient.utils;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;

import tarun.djangorestclient.com.djangorestclient.R;

/**
 * Utility class with common http related utility methods.
 */

public class HttpUtil {

    private static final String TAG = HttpUtil.class.getSimpleName();

    /**
     * This method encodes the Authentication credentials provided as input in simple String format.
     * @return Returns the Base64 encoded credentials.
     */
    public static String getBase64EncodedAuthCreds(Context context, String userName, String password) {
        String credsToEncode = context.getString(R.string.username_pwd_append, userName, password);

        // Sending side
        byte[] data = new byte[0];
        try {
            data = credsToEncode.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Error encoding data.", e);
        }
        String encodedCreds = Base64.encodeToString(data, Base64.DEFAULT);

        return context.getString(R.string.space_separated_strings,"Basic", encodedCreds);
    }

}
