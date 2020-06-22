package tarun.djangorestclient.com.djangorestclient.model.entity;

import java.util.ArrayList;
import java.util.List;

import androidx.room.Embedded;
import androidx.room.Relation;

public class RequestWithHeaders {
    @Embedded
    private Request request;

    @Relation(
            parentColumn = "requestId",
            entityColumn = "parentRequestId"
    )
    private List<Header> headers;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public List<Header> getHeaders() {
        return headers;
    }

    public void setHeaders(ArrayList<Header> headers) {
        this.headers = headers;
    }
}
