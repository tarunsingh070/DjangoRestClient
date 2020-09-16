/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, June 2020.
 */
package tarun.djangorestclient.com.djangorestclient.model

import androidx.room.TypeConverter
import tarun.djangorestclient.com.djangorestclient.model.entity.Header.HeaderType
import tarun.djangorestclient.com.djangorestclient.model.entity.Request.RequestType
import java.util.*

object Converters {
    @JvmStatic
    @TypeConverter
    fun fromRequestTypeString(requestTypeString: String?): RequestType? {
        return if (requestTypeString == null) null
        else RequestType.valueOf(requestTypeString)
    }

    @JvmStatic
    @TypeConverter
    fun requestTypeToRequestTypeString(requestType: RequestType?) = requestType?.toString()

    // Todo : Currently, we cannot use the following 2 converters because in case of a custom header,
    //  the value cannot be one of the HeaderType values and instead will be a custom string provided by user.
    @TypeConverter
    fun fromHeaderTypeString(headerTypeString: String?): HeaderType? {
        return if (headerTypeString == null) null
        else HeaderType.valueOf(headerTypeString)
    }

    @TypeConverter
    fun headerTypeToHeaderTypeString(headerType: HeaderType?) = headerType?.toString()

    @JvmStatic
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null
        else Date(value)
    }

    @JvmStatic
    @TypeConverter
    fun dateToTimestamp(date: Date?) = date?.time
}