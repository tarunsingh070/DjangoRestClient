package tarun.djangorestclient.com.djangorestclient.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import tarun.djangorestclient.com.djangorestclient.R;

/**
 * Utility class with miscellaneous utility methods.
 */

public class MiscUtil {

    /**
     * Utility method to show or hide soft keyboard.
     *
     * @param shouldShow if true, keyboard is shown, hidden otherwise.
     */
    public static void showOrHideKeyboard(Context context, View view, boolean shouldShow) {
        // FixMe: Doesn't seem to work currently.
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (shouldShow) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

//        // Check if no view has focus:
//        View view = activity.getCurrentFocus();
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
////            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
//            view.clearFocus();
//        }
    }

    public static void showSpinner(Activity activity) {
        final View progressBar = activity.findViewById(R.id.progress_layout);
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

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
}
