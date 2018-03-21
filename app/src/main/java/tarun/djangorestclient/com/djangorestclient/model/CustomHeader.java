/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.model;

/**
 * Model class subclassing the Header class with more Custom header fields.
 */

public class CustomHeader extends Header {

    private String customHeaderType;

    public CustomHeader(String customHeaderType, String headerValue) {
        super(HeaderType.CUSTOM, headerValue);
        this.customHeaderType = customHeaderType;
    }

    public String getCustomHeaderType() {
        return customHeaderType;
    }

    public void setCustomHeaderType(String customHeaderType) {
        this.customHeaderType = customHeaderType;
    }
}
