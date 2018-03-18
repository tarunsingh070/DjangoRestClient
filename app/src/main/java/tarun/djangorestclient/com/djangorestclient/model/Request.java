package tarun.djangorestclient.com.djangorestclient.model;

import java.util.ArrayList;

/**
 * Model class to store all information for a request.
 */

public class Request {

    public enum RequestType {
        GET, POST, PUT, DELETE, PATCH
    }

    private String url;
    private RequestType requestType;
    private ArrayList<Header> headers = new ArrayList<>();
    private String body;

    public Request() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public ArrayList<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(ArrayList<Header> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
