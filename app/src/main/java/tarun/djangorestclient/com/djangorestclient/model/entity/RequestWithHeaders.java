/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, June 2020.
 */

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

    // We need these equals and hashcode methods to identify if the original live data object has been
    // modified or some other object while observing a live instance of this class.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestWithHeaders that = (RequestWithHeaders) o;

        return getRequest().equals(that.getRequest());
    }

    @Override
    public int hashCode() {
        return getRequest().hashCode();
    }
}
