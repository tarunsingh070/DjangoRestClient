package tarun.djangorestclient.com.djangorestclient.model;

import java.util.Date;

import androidx.room.TypeConverter;
import tarun.djangorestclient.com.djangorestclient.model.entity.Header;
import tarun.djangorestclient.com.djangorestclient.model.entity.Request;

public class Converters {
    @TypeConverter
    public static Request.RequestType fromRequestTypeString(String requestTypeString) {
        return requestTypeString == null ? null : Request.RequestType.valueOf(requestTypeString);
    }

    @TypeConverter
    public static String requestTypeToRequestTypeString(Request.RequestType requestType) {
        return requestType == null ? null : requestType.toString();
    }

    // Todo : Currently, we cannot use the following 2 converters because in case of a custom header,
    //  the value cannot be one of the HeaderType values and instead will be a custom string provided by user.
    @TypeConverter
    public static Header.HeaderType fromHeaderTypeString(String headerTypeString) {
        return headerTypeString == null ? null : Header.HeaderType.valueOf(headerTypeString);
    }

    @TypeConverter
    public static String headerTypeToHeaderTypeString(Header.HeaderType headerType) {
        return headerType == null ? null : headerType.toString();
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
