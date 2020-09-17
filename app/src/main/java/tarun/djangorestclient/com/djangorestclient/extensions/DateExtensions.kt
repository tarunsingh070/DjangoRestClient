package tarun.djangorestclient.com.djangorestclient.extensions

import java.text.SimpleDateFormat
import java.util.*

const val DATE_AND_TIME_PATTERN_1 = "dd MMMM, yyyy"

/**
 * Converts a {@link Date} object into a string representation of date in the format
 * {@link #DATE_AND_TIME_PATTERN_1}.
 *
 * @param date The {@link Date} object to convert into string representation of date.
 * @return The string representation of date from the {@link Date} object provided.
 */
fun Date.getFormattedDate(): String {
    val simpleDateFormat = SimpleDateFormat(DATE_AND_TIME_PATTERN_1,
            Locale.getDefault())
    return simpleDateFormat.format(this)
}