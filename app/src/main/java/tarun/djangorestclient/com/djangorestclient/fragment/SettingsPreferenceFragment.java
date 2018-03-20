package tarun.djangorestclient.com.djangorestclient.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.TextUtils;

import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.utils.RestClient;

/**
 * Settings Preference fragment allows user to change rest client configurations as desired.
 */
public class SettingsPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey);

        sharedPreferences = getPreferenceManager().getSharedPreferences();
        initPreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        setTimeoutPrefSummary(key);
        updateRestClientConfiguration();
    }

    @Override
    public void onPause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    /**
     * Initialize preferences.
     */
    private void initPreferences() {
        setTimeoutPrefSummary(getString(R.string.key_timeout_connect_preference));
        setTimeoutPrefSummary(getString(R.string.key_timeout_read_preference));
        setTimeoutPrefSummary(getString(R.string.key_timeout_write_preference));
    }

    /**
     * Set summary for a timeout preference type to be the user set timeout value.
     * @param preferenceKey Preference Key corresponding to the timeout preference.
     */
    private void setTimeoutPrefSummary(String preferenceKey) {
        Preference timeoutPref = findPreference(preferenceKey);
        String timeoutPrefValue = sharedPreferences.getString(preferenceKey, "");
        if (!TextUtils.isEmpty(timeoutPrefValue)) {
            timeoutPref.setSummary(getString(R.string.summary_timeout_preferences, Integer.valueOf(timeoutPrefValue)));
        }
    }

    /**
     * Fetch all updated configuration values from shared preferences and update the Rest client configuration.
     */
    private void updateRestClientConfiguration() {
        String timeoutConnectPrefValueString = sharedPreferences.getString(getString(R.string.key_timeout_connect_preference), "");
        String timeoutReadPrefValueString = sharedPreferences.getString(getString(R.string.key_timeout_read_preference), "");
        String timeoutWritePrefValueString = sharedPreferences.getString(getString(R.string.key_timeout_write_preference), "");

        RestClient.updateOkHttpClientConfigurations(timeoutConnectPrefValueString, timeoutReadPrefValueString, timeoutWritePrefValueString);
    }
}
