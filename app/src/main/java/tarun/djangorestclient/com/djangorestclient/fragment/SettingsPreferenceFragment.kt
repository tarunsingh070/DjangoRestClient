/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */
package tarun.djangorestclient.com.djangorestclient.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import tarun.djangorestclient.com.djangorestclient.R

/**
 * Settings Preference fragment allows user to change rest client configurations as desired.
 */
class SettingsPreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    companion object {
        const val TAG = "SettingsPreferenceFragm"

        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment SettingsPreferenceFragment.
         */
        fun newInstance(): SettingsPreferenceFragment {
            return SettingsPreferenceFragment()
        }
    }

    private var sharedPreferences: SharedPreferences? = null

    /**
     * A [Preference.OnPreferenceChangeListener] for when a shared preference value is changed.
     */
    private var preferenceChangeListener: Preference.OnPreferenceChangeListener? = null
        get() {
            if (field == null) {
                field = Preference.OnPreferenceChangeListener { preference: Preference?, newValue: Any? ->
                    if (newValue is String && TextUtils.isDigitsOnly(newValue as String?)) {
                        return@OnPreferenceChangeListener true
                    }

                    Toast.makeText(requireContext(), R.string.invalid_timeout_value_error, Toast.LENGTH_LONG).show()
                    false
                }
            }
            return field
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey)
        sharedPreferences = preferenceManager.sharedPreferences
        initPreferences()
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        key?.let { setTimeoutPrefSummary(it) }
    }

    override fun onPause() {
        sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    /**
     * Initialize preferences.
     */
    private fun initPreferences() {
        setTimeoutPrefSummary(getString(R.string.key_timeout_connect_preference))
        setTimeoutPrefSummary(getString(R.string.key_timeout_read_preference))
        setTimeoutPrefSummary(getString(R.string.key_timeout_write_preference))
    }

    /**
     * Set summary for a timeout preference type to be the user set timeout value.
     *
     * @param preferenceKey Preference Key corresponding to the timeout preference.
     */
    private fun setTimeoutPrefSummary(preferenceKey: String) {
        val timeoutPref = findPreference<Preference>(preferenceKey)
        val timeoutPrefValue = sharedPreferences?.getString(preferenceKey, "")

        timeoutPref?.summary =
                if (TextUtils.isEmpty(timeoutPrefValue)) getString(R.string.value_not_set)
                else getString(R.string.summary_timeout_preferences, timeoutPrefValue)
        timeoutPref?.onPreferenceChangeListener = preferenceChangeListener
    }
}