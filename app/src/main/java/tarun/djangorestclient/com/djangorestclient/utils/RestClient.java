package tarun.djangorestclient.com.djangorestclient.utils;

import android.text.TextUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import tarun.djangorestclient.com.djangorestclient.model.Header;

/**
 * This network utility class contains methods to make all supported types of rest requests.
 */

public class RestClient {

    private static OkHttpClient client = new OkHttpClient.Builder().build();

    /**
     * Update the OkHttpClientConfigurations based on the values received.
     */
    public static void updateOkHttpClientConfigurations(String timeoutConnectPrefValueString, String timeoutReadPrefValueString
            , String timeoutWritePrefValueString) {

        boolean isTimeOutConnectPrefSet = !TextUtils.isEmpty(timeoutConnectPrefValueString);
        boolean isTimeOutReadPrefSet = !TextUtils.isEmpty(timeoutReadPrefValueString);
        boolean isTimeOutWritePrefSet = !TextUtils.isEmpty(timeoutWritePrefValueString);

        if (isTimeOutConnectPrefSet && isTimeOutReadPrefSet && isTimeOutWritePrefSet) {
            // Update Connect timeout, Read timeout and Write timeout configurations.
            client = client.newBuilder()
                    .connectTimeout(Integer.valueOf(timeoutConnectPrefValueString), TimeUnit.SECONDS)
                    .readTimeout(Integer.valueOf(timeoutReadPrefValueString), TimeUnit.SECONDS)
                    .writeTimeout(Integer.valueOf(timeoutWritePrefValueString), TimeUnit.SECONDS)
                    .build();
        } else if (isTimeOutConnectPrefSet && isTimeOutReadPrefSet) {
            // Update Connect timeout and Read timeout configurations.
            client = client.newBuilder()
                    .connectTimeout(Integer.valueOf(timeoutConnectPrefValueString), TimeUnit.SECONDS)
                    .readTimeout(Integer.valueOf(timeoutReadPrefValueString), TimeUnit.SECONDS)
                    .build();
        } else if (isTimeOutConnectPrefSet && isTimeOutWritePrefSet) {
            // Update Connect timeout and Write timeout configurations.
            client = client.newBuilder()
                    .connectTimeout(Integer.valueOf(timeoutConnectPrefValueString), TimeUnit.SECONDS)
                    .writeTimeout(Integer.valueOf(timeoutWritePrefValueString), TimeUnit.SECONDS)
                    .build();
        } else if (isTimeOutReadPrefSet && isTimeOutWritePrefSet) {
            // Update Read timeout and Write timeout configurations.
            client = client.newBuilder()
                    .readTimeout(Integer.valueOf(timeoutReadPrefValueString), TimeUnit.SECONDS)
                    .writeTimeout(Integer.valueOf(timeoutWritePrefValueString), TimeUnit.SECONDS)
                    .build();
        } else if (isTimeOutConnectPrefSet) {
            // Update Connect timeout configuration.
            client = client.newBuilder()
                    .connectTimeout(Integer.valueOf(timeoutConnectPrefValueString), TimeUnit.SECONDS)
                    .build();
        } else if (isTimeOutReadPrefSet) {
            // Update Read timeout configuration.
            client = client.newBuilder()
                    .readTimeout(Integer.valueOf(timeoutReadPrefValueString), TimeUnit.SECONDS)
                    .build();
        } else {
            // Update Write timeout configuration.
            client = client.newBuilder()
                    .writeTimeout(Integer.valueOf(timeoutWritePrefValueString), TimeUnit.SECONDS)
                    .build();
        }

    }

    public static Call get(String url, List<Header> headers, Callback callback) {
        Request request = new Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers)).get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call post(String url, List<Header> headers, String body, Callback callback) {
        Request request = new Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers)).post
                (body == null ? Util.EMPTY_REQUEST : RequestBody.create(null, body)).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call head(String url, List<Header> headers, Callback callback) {
        Request request = new Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers)).head()
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call put(String url, List<Header> headers, String body, Callback callback) {
        Request request = new Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers)).put
                (body == null ? Util.EMPTY_REQUEST : RequestBody.create(null, body)).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call delete(String url, List<Header> headers, String body, Callback callback) {
        Request request = new Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers)).delete
                (body == null ? null : RequestBody.create(null, body)).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    public static Call patch(String url, List<Header> headers, String body, Callback callback) {
        Request request = new Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers)).patch
                (body == null ? Util.EMPTY_REQUEST : RequestBody.create(null, body)).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

}
