package tarun.djangorestclient.com.djangorestclient.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import tarun.djangorestclient.com.djangorestclient.R
import java.util.regex.Pattern

/**
 * Utility method to explicitly hide soft keyboard.
 */
fun Activity.hideKeyboard() {
    // Check if no view has focus:
    val view: View? = currentFocus
    view?.apply {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        view.clearFocus()
    }
}

/**
 * Show the cyclic animation progress bar.
 */
fun Activity.showSpinner() {
    val progressBar: View = findViewById(R.id.progress_layout)
    progressBar.visibility = View.VISIBLE
}

/**
 * Hide the cyclic animation progress bar on UI thread.
 */
fun Activity.hideSpinner() {
    val progressBar: View = findViewById(R.id.progress_layout)
    runOnUiThread {
        progressBar.visibility = View.GONE
    }
}

/**
 * Check if a string value is actually a valid integer or not.
 *
 * @return True if it is an integer, false otherwise.
 */
fun String.isNumber(): Boolean {
    return try {
        toInt()
        // is an integer!
        true
    } catch (e: NumberFormatException) {
        // not an integer!
        false
    } catch (e: NullPointerException) {
        false
    }
}

/**
 * Displays a toast of [Toast.LENGTH_SHORT] duration.
 *
 * @param message The resource Id of the message to show.
 */
fun Context.displayShortToast(message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Displays a toast of [Toast.LENGTH_SHORT] duration.
 *
 * @param message The String message to show.
 */
fun Context.displayShortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Displays a toast of [Toast.LENGTH_LONG] duration.
 *
 * @param message The resource Id of the message to show.
 */
fun Context.displayLongToast(message: Int) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Displays a toast of [Toast.LENGTH_LONG] duration.
 *
 * @param message The String message to show.
 */
fun Context.displayLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Checks if the text passed in contains whitespaces or not.
 *
 * @return True if the text contains whitespaces, false otherwise.
 */
fun String.containsWhiteSpaces(): Boolean {
    val pattern = Pattern.compile("\\s")
    val matcher = pattern.matcher(this)
    return matcher.find()
}

/**
 * Toggles the drawer of the [DrawerLayout]
 * @param drawer Drawer view to check it's current state
 */
fun DrawerLayout.toggleDrawer(drawer: View) {
    if (isDrawerOpen(drawer)) {
        closeDrawer(GravityCompat.START)
    } else {
        openDrawer(GravityCompat.START)
    }
}