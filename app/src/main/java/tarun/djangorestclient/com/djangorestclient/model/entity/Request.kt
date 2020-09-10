/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */
package tarun.djangorestclient.com.djangorestclient.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

/**
 * Model class to store all information for a request.
 */
@Entity(tableName = "request")
data class Request(@PrimaryKey(autoGenerate = true)
                   var requestId: Long = 0,
                   var url: String = "",
                   @Ignore
                   var headers: ArrayList<Header> = ArrayList<Header>(),
                   var body: String? = null,

                   @ColumnInfo(name = "is_in_history")
                   var isInHistory: Boolean = false,

                   @ColumnInfo(name = "is_saved")
                   var isSaved: Boolean = false,

                   @ColumnInfo(name = "updated_at_timestamp")
                   var updatedAt: Date? = null) {

    enum class RequestType {
        GET, POST, PUT, DELETE, HEAD, PATCH
    }

    @ColumnInfo(name = "request_type")
    lateinit var requestType: RequestType

    /**
     * Creates and returns a copy of the current object.
     */
    fun copyRequest(): Request {
        val copy = copy()
        copy.requestType = requestType
        return copy
    }

    fun clearIds() {
        requestId = 0
        for (header in headers) {
            header.clearHeaderId()
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val request = other as Request
        if (requestId != request.requestId) return false
        if (url != request.url) return false
        if (requestType != request.requestType) return false
        return if (body != null) body == request.body else request.body == null
    }

    override fun hashCode(): Int {
        var result = requestId.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + (body?.hashCode() ?: 0)
        result = 31 * result + requestType.hashCode()
        return result
    }
}