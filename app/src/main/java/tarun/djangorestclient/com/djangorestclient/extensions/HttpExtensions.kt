package tarun.djangorestclient.com.djangorestclient.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import org.json.JSONException
import org.json.JSONObject

/**
 * Checks the internet connectivity status of user's device.
 *
 * @return True if connected, False otherwise.
 */
fun Context.isNetworkAvailable(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // FixMe : This seems like a stupid way to just find out if device is connected to internet or not.
        //  Cannot find a better way at this time. Try using ConnectivityManager.NetworkCallback class
        //  in future to listen for connectivity events and update flag accordingly.
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        if (capabilities != null) {
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                else -> false
            }
        }

        return false
    } else {
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting
    }
}

/**
 * Formats a supposedly unformatted JSON input text and returns a formatted version text.
 *
 * @return The formatted JSON string or the same unformatted text if JSON string couldn't
 * be parsed due to it being not a valid JSON data or some other reason.
 */
fun String.getFormattedJsonText(): String {
    return try {
        val jsonObject = JSONObject(this)
        jsonObject.toString(4)
    } catch (e: JSONException) {
        e.printStackTrace()
        // If data couldn't be parsed as JSON data, then simply return the text as it is.
        this
    }
}