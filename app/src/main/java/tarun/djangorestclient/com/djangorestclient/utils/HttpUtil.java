/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;

import okhttp3.Headers;
import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.model.entity.Header;

/**
 * Utility class with common http related utility methods.
 */

public class HttpUtil {

    private static final String TAG = HttpUtil.class.getSimpleName();

    /**
     * This method encodes the Authentication credentials provided as input in simple String format.
     *
     * @return Returns the Base64 encoded credentials.
     */
    public static String getBase64EncodedAuthCreds(Context context, String userName, String password) {
        String credsToEncode = context.getString(R.string.username_pwd_append, userName, password);

        // Sending side
        byte[] data;
        data = credsToEncode.getBytes(StandardCharsets.UTF_8);
        String encodedCreds = Base64.encodeToString(data, Base64.NO_WRAP | Base64.URL_SAFE);

        return context.getString(R.string.space_separated_strings, "Basic", encodedCreds);
    }

    /**
     * This method decodes the Authentication credentials provided as input in simple String format.
     *
     * @return Returns the Base64 decoded credentials in the format "username:password" .
     */
    public static String getBase64DecodedAuthCreds(String encodedCredentials) {
        // Consider string after "Basic " of the header value.
        String credsToDecode = encodedCredentials.substring("Basic ".length());

        // Receiving side
        byte[] data = Base64.decode(credsToDecode, Base64.NO_WRAP | Base64.URL_SAFE);

        return new String(data, StandardCharsets.UTF_8);
    }

    /**
     * Parse the list of headers added by user to be sent as part of rest request.
     *
     * @param headers: List of headers added by user.
     * @return List of okhttp3 headers.
     */
    public static Headers getParsedHeaders(List<Header> headers) {
        Headers.Builder headerBuilder = new Headers.Builder();
        for (Header header : headers) {
            String headerName;
            if (header.getHeaderTypeEnum() == Header.HeaderType.AUTHORIZATION_BASIC) {
                headerName = "Authorization";
            } else {
                headerName = header.getHeaderType();
            }

            headerBuilder.add(headerName, header.getHeaderValue());
        }
        return headerBuilder.build();
    }

    /**
     * Checks the internet connectivity status of user's device.
     *
     * @return True if connected, False otherwise.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
        return isConnected;
    }

    /**
     * Formats a supposedly unformatted JSON input text and returns a formatted version text.
     *
     * @param text The unformatted JSON input text to be formatted.
     * @return The formatted JSON string or the same unformatted text if JSON string couldn't
     * be parsed due to it being not a valid JSON data or some other reason.
     */
    public static String getFormattedJsonText(String text) {
        try {
            JSONObject jsonObject = new JSONObject(text);
            return jsonObject.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
            // If data couldn't be parsed as JSON data, then simply return the text as it is.
            return text;
        }
    }

}
