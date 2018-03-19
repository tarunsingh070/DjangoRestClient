package tarun.djangorestclient.com.djangorestclient.utils;

import java.util.List;

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

    private static OkHttpClient client = new OkHttpClient();

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
