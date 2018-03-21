/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.model;

/**
 * Model class to store header data.
 */

public class Header {

    // Enum to define all supported Header types.
    public enum HeaderType {
        ACCEPT("Accept"),
        CONTENT_TYPE("Content-Type"),
        AUTHORIZATION("Authorization"),
        AUTHORIZATION_BASIC("Authorization (Basic)"),
        CUSTOM("Custom");

        // Name to be returned for each enum.
        private String name;

        HeaderType(String name){
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private HeaderType headerType;
    private String headerValue;

    public Header() {
    }

    public Header(HeaderType headerType, String headerValue) {
        this.headerType = headerType;
        this.headerValue = headerValue;
    }

    public HeaderType getHeaderType() {
        return headerType;
    }

    public void setHeaderType(HeaderType headerType) {
        this.headerType = headerType;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }
}
