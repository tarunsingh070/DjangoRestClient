/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.model.entity;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Model class to store all information for a request.
 */

@Entity(tableName = "request")
public class Request {

    public enum RequestType {
        GET, POST, PUT, DELETE, HEAD, PATCH
    }

    @PrimaryKey(autoGenerate = true)
    private long requestId;

    @NonNull
    private String url;

    @NonNull
    @ColumnInfo(name = "request_type")
    private RequestType requestType;

    @Ignore
    private ArrayList<Header> headers = new ArrayList<>();

    private String body;

    @ColumnInfo(name = "is_in_history")
    private boolean isInHistory;

    @ColumnInfo(name = "is_saved")
    private boolean isSaved;

    @ColumnInfo(name = "updated_at_timestamp")
    private Date updatedAt;

    public Request() {
    }

    /**
     * Copy Constructor.
     *
     * @param request The {@link Request} instance to be copied.
     */
    public Request(Request request) {
        this.requestId = request.requestId;
        this.url = request.url;
        this.requestType = request.requestType;
        this.headers = request.headers;
        this.body = request.body;
        this.isInHistory = request.isInHistory;
        this.isSaved = request.isSaved;
        this.updatedAt = request.updatedAt;
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

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public void clearIds() {
        requestId = 0;
        for (Header header : headers) {
            header.clearHeaderId();
        }
    }

    public boolean isInHistory() {
        return isInHistory;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setInHistory(boolean inHistory) {
        isInHistory = inHistory;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        if (getRequestId() != request.getRequestId()) return false;
        if (!getUrl().equals(request.getUrl())) return false;
        if (getRequestType() != request.getRequestType()) return false;
        return getBody() != null ? getBody().equals(request.getBody()) : request.getBody() == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (getRequestId() ^ (getRequestId() >>> 32));
        result = 31 * result + getUrl().hashCode();
        result = 31 * result + getRequestType().hashCode();
        result = 31 * result + (getBody() != null ? getBody().hashCode() : 0);
        return result;
    }
}
