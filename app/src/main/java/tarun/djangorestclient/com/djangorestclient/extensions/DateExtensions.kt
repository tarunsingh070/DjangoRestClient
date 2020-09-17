package tarun.djangorestclient.com.djangorestclient.extensions

import java.text.SimpleDateFormat
import java.util.*

const val DATE_AND_TIME_PATTERN_1 = "dd MMMM, yyyy"

/**
 * Converts a [Date] object into a string representation of date in the format
 * [DATE_AND_TIME_PATTERN_1].
 *
 * @return The string representation of date from the [Date] object provided.
 */
fun Date.getFormattedDate(): String {
    val simpleDateFormat = SimpleDateFormat(DATE_AND_TIME_PATTERN_1,
            Locale.getDefault())
    return simpleDateFormat.format(this)
}