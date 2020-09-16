/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.model;

/**
 * Model class to store all information received as response for a particular request.
 */

public class RestResponse {

    private int responseCode;
    private long responseTime;
    private String url;
    private CharSequence responseHeaders;
    private String responseBody;

    /**
     * Constructor.
     *
     * @param responseCode    The response code received.
     * @param responseTime    The response time.
     * @param url             The URL of the response.
     * @param responseHeaders The list of response Headers received as a String.
     * @param responseBody    The response body received.
     */
    public RestResponse(int responseCode, long responseTime, String url, CharSequence responseHeaders, String responseBody) {
        this.responseCode = responseCode;
        this.responseTime = responseTime;
        this.url = url;
        this.responseHeaders = responseHeaders;
        this.responseBody = responseBody;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public CharSequence getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(CharSequence responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
