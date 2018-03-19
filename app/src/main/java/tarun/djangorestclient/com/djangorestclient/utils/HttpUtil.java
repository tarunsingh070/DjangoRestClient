package tarun.djangorestclient.com.djangorestclient.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.List;

import okhttp3.Headers;
import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.model.CustomHeader;
import tarun.djangorestclient.com.djangorestclient.model.Header;
import tarun.djangorestclient.com.djangorestclient.model.Header.HeaderType;

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

    /**
     * Parse the list of headers added by user to be sent as part of rest request.
     * @param headers: List of headers added by user.
     * @return: List of okhttp3 headers.
     */
    public static Headers getParsedHeaders(List<Header> headers) {
        Headers.Builder headerBuilder = new Headers.Builder();
        for (Header header : headers) {
            String headerName;
            if (header.getHeaderType() == HeaderType.AUTHORIZATION_BASIC) {
                headerName = "Authorization";
            } else if (header.getHeaderType() == HeaderType.CUSTOM) {
                headerName = ((CustomHeader) header).getCustomHeaderType();
            } else {
                headerName = header.getHeaderType().toString();
            }

            headerBuilder.add(headerName, header.getHeaderValue());
        }
        return headerBuilder.build();
    }

    /**
     * Checks the internet connectivity status of user's device.
     * @return True if connected, False otherwise.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
        return isConnected;
    }

}
