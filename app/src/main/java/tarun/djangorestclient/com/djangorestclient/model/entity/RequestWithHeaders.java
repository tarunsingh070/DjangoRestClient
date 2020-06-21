package tarun.djangorestclient.com.djangorestclient.model.entity;

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

    public List<Header> getHeaders() {
        return headers;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void setHeaders(List<Header> headers) {
        this.headers = headers;
    }
}
