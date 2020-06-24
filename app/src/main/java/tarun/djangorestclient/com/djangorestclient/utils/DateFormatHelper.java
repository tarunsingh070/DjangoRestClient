package tarun.djangorestclient.com.djangorestclient.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Convenience class for Date related functions.
 */
public abstract class DateFormatHelper {
    private static final String TAG = "DateFormatHelper";

    public static final String DATE_AND_TIME_PATTERN_1 = "dd MMMM, yyyy";

    /**
     * Converts a {@link Date} object into a string representation of date in the format
     * {@link #DATE_AND_TIME_PATTERN_1}.
     *
     * @param date The {@link Date} object to convert into string representation of date.
     * @return The string representation of date from the {@link Date} object provided.
     */
    public static String getString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_AND_TIME_PATTERN_1,
                Locale.getDefault());
        return simpleDateFormat.format(date);
    }

    /**
     * Returns the current date.
     *
     * @return The current date.
     */
    public static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }
}
