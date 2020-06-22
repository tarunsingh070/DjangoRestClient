/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.model.entity;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Model class to store header data.
 */

@Entity(tableName = "header", foreignKeys = {
        @ForeignKey(onDelete = CASCADE, entity = Request.class,
                parentColumns = "requestId", childColumns = "parentRequestId")},
        indices = {
                @Index("parentRequestId"),
        })
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

        HeaderType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @PrimaryKey(autoGenerate = true)
    private long headerId;

    @NonNull
    @ColumnInfo(name = "header_type")
    private String headerType;

    @NonNull
    @ColumnInfo(name = "header_value")
    private String headerValue;

    private long parentRequestId;

    public Header() {
    }

    public Header(@NonNull String headerType, @NonNull String headerValue) {
        this.headerType = headerType;
        this.headerValue = headerValue;
    }

//    public Header(@NonNull String headerType, @NonNull String headerValue, long parentRequestId) {
//        this.headerType = headerType;
//        this.headerValue = headerValue;
//        this.parentRequestId = parentRequestId;
//    }

    public long getHeaderId() {
        return headerId;
    }

    public void setHeaderId(long headerId) {
        this.headerId = headerId;
    }

    public String getHeaderType() {
        return headerType;
    }

    public void setHeaderType(String headerType) {
        this.headerType = headerType;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public void setHeaderValue(String headerValue) {
        this.headerValue = headerValue;
    }

    public long getParentRequestId() {
        return parentRequestId;
    }

    public void setParentRequestId(long parentRequestId) {
        this.parentRequestId = parentRequestId;
    }

    /**
     * Get the corresponding HeaderTypeEnum instance based on the String parameter received.
     *
     * @return HeaderType enum instance.
     */
    public HeaderType getHeaderTypeEnum() {
        for (HeaderType type : HeaderType.values()) {
            if (TextUtils.equals(headerType, type.toString())) {
                return type;
            }
        }

        return HeaderType.CUSTOM;
    }
}
