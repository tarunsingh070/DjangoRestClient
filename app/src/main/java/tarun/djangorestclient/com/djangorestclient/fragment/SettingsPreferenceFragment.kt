/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import tarun.djangorestclient.com.djangorestclient.R;

/**
 * Settings Preference fragment allows user to change rest client configurations as desired.
 */
public class SettingsPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = "SettingsPreferenceFragm";

    private SharedPreferences sharedPreferences;
    private Preference.OnPreferenceChangeListener preferenceChangeListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment SettingsPreferenceFragment.
     */
    public static SettingsPreferenceFragment newInstance() {
        return new SettingsPreferenceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

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
     *
     * @param preferenceKey Preference Key corresponding to the timeout preference.
     */
    private void setTimeoutPrefSummary(String preferenceKey) {
        Preference timeoutPref = findPreference(preferenceKey);
        String timeoutPrefValue = sharedPreferences.getString(preferenceKey, "");

        timeoutPref.setSummary(TextUtils.isEmpty(timeoutPrefValue) ?
                getString(R.string.value_not_set) :
                getString(R.string.summary_timeout_preferences, timeoutPrefValue));

        timeoutPref.setOnPreferenceChangeListener(getPreferenceChangeListener());
    }

    /**
     * Create and return a listener for when a shared preference value is changed.
     *
     * @return The instance of {@link Preference.OnPreferenceChangeListener} created.
     */
    private Preference.OnPreferenceChangeListener getPreferenceChangeListener() {
        if (preferenceChangeListener == null) {
            preferenceChangeListener = (preference, newValue) -> {
                if (newValue instanceof String && TextUtils.isDigitsOnly((String) newValue)) {
                    return true;
                }

                Toast.makeText(requireContext(), R.string.invalid_timeout_value_error, Toast.LENGTH_LONG).show();
                return false;
            };
        }

        return preferenceChangeListener;
    }
}
