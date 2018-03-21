package tarun.djangorestclient.com.djangorestclient.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import tarun.djangorestclient.com.djangorestclient.R;

/**
 * Utility class with miscellaneous utility methods.
 */

public class MiscUtil {

    /**
     * Utility method to explicitly hide soft keyboard.
     */
    public static void hideKeyboard(Context context, Activity activity) {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

    /**
     * Show the cyclic animation progress bar.
     */
    public static void showSpinner(Activity activity) {
        final View progressBar = activity.findViewById(R.id.progress_layout);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide the cyclic animation progress bar on UI thread.
     */
    public static void hideSpinner(Activity activity) {
        final View progressBar = activity.findViewById(R.id.progress_layout);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Check if a string value is actually a valid integer or not.
     * @param str String value to check.
     * @return True if it is an integer, false otherwise.
     */
    public static boolean isNumber(String str) {
        try {
            Integer.parseInt(str);
            // is an integer!
        } catch (NumberFormatException e) {
            // not an integer!
        } catch (NullPointerException e) {
            return false;
        }

        return true;
    }

    public static void displayShortToast(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void displayLongToast(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void displayShortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void displayLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
