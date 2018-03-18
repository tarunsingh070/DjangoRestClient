package tarun.djangorestclient.com.djangorestclient.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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

}
