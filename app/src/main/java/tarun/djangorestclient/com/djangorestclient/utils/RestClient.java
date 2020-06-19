/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.utils;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.model.Header;

/**
 * This network utility class contains methods to make all supported types of rest requests.
 */

public class RestClient {

    private final Context context;
    private final SharedPreferences sharedPreferences;
    private OkHttpClient client = new OkHttpClient.Builder().build();

    public RestClient(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Update the OkHttpClientConfigurations based on the shared preference values.
     */
    private void updateClient() {
        // Fetch the timeout values from shared preferences.
        String timeoutConnectPrefValueString = sharedPreferences.getString(context.getString(R.string.key_timeout_connect_preference), "");
        String timeoutReadPrefValueString = sharedPreferences.getString(context.getString(R.string.key_timeout_read_preference), "");
        String timeoutWritePrefValueString = sharedPreferences.getString(context.getString(R.string.key_timeout_write_preference), "");

        // Check if shared preference values for each preference has been explicitly set by user or not.
        // Also verify if the value set is actually a valid number or not.
        boolean isTimeOutConnectPrefSet = (!TextUtils.isEmpty(timeoutConnectPrefValueString) && MiscUtil.isNumber(timeoutConnectPrefValueString));
        boolean isTimeOutReadPrefSet = (!TextUtils.isEmpty(timeoutReadPrefValueString) && MiscUtil.isNumber(timeoutReadPrefValueString));
        boolean isTimeOutWritePrefSet = (!TextUtils.isEmpty(timeoutWritePrefValueString) && MiscUtil.isNumber(timeoutWritePrefValueString));

        OkHttpClient.Builder builder = client.newBuilder();

        // Re-configure the builder based on preference values set (if any) in the settings.
        if (isTimeOutConnectPrefSet) {
            builder.connectTimeout(Integer.valueOf(timeoutConnectPrefValueString), TimeUnit.SECONDS);
        }

        if (isTimeOutReadPrefSet) {
            builder.readTimeout(Integer.valueOf(timeoutReadPrefValueString), TimeUnit.SECONDS);
        }

        if (isTimeOutWritePrefSet) {
            builder.writeTimeout(Integer.valueOf(timeoutWritePrefValueString), TimeUnit.SECONDS);
        }

        client = builder.build();
    }

    public void get(String url, List<Header> headers, Callback callback) {
        Request request = new Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers)).get()
                .build();
        updateClient();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void post(String url, List<Header> headers, String body, Callback callback) {
        Request request = new Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers)).post
                (body == null ? Util.EMPTY_REQUEST : RequestBody.create(null, body)).build();
        updateClient();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void head(String url, List<Header> headers, Callback callback) {
        Request request = new Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers)).head()
                .build();
        updateClient();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void put(String url, List<Header> headers, String body, Callback callback) {
        Request request = new Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers)).put
                (body == null ? Util.EMPTY_REQUEST : RequestBody.create(null, body)).build();
        updateClient();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void delete(String url, List<Header> headers, String body, Callback callback) {
        Request request = new Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers)).delete
                (body == null ? null : RequestBody.create(null, body)).build();
        updateClient();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public void patch(String url, List<Header> headers, String body, Callback callback) {
        Request request = new Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers)).patch
                (body == null ? Util.EMPTY_REQUEST : RequestBody.create(null, body)).build();
        updateClient();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

}
