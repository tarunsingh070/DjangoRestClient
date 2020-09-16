/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */
package tarun.djangorestclient.com.djangorestclient.activity

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import tarun.djangorestclient.com.djangorestclient.R
import tarun.djangorestclient.com.djangorestclient.databinding.ActivityHomeBinding
import tarun.djangorestclient.com.djangorestclient.fragment.AboutFragment
import tarun.djangorestclient.com.djangorestclient.fragment.RequestFragment
import tarun.djangorestclient.com.djangorestclient.fragment.RequestFragment.OnResponseReceivedListener
import tarun.djangorestclient.com.djangorestclient.fragment.RestCallsFragment
import tarun.djangorestclient.com.djangorestclient.fragment.SettingsPreferenceFragment
import tarun.djangorestclient.com.djangorestclient.fragment.requestsList.RequestsListFragment
import tarun.djangorestclient.com.djangorestclient.fragment.requestsList.RequestsListFragment.RequestsListFragmentListener
import tarun.djangorestclient.com.djangorestclient.model.RestResponse
import tarun.djangorestclient.com.djangorestclient.utils.MiscUtil

/**
 * This is the home activity which would allow user to navigate to other screens through its navigation drawer.
 */
class HomeActivity : AppCompatActivity(), OnResponseReceivedListener, RequestsListFragmentListener {
    companion object {
        private val TAG = HomeActivity::class.java.simpleName
    }

    private var selectedNavMenuItemId = 0
    private lateinit var binding: ActivityHomeBinding
    private var requestId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        supportActionBar?.elevation = 0f

        setupNavigationView()
    }

    /**
     * Setup all navigation view related actions here.
     */
    private fun setupNavigationView() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem: MenuItem ->
            // close drawer when an item is tapped
            binding.drawerLayout.closeDrawers()

            // Simply return if selected menu item was already selected.
            if (menuItem.itemId == selectedNavMenuItemId) {
                return@setNavigationItemSelectedListener true
            }

            // store the selected menuItemId.
            selectedNavMenuItemId = menuItem.itemId

            // set item as selected to persist highlight
            menuItem.isChecked = true
            title = menuItem.title
            MiscUtil.hideKeyboard(this@HomeActivity, this@HomeActivity)

            // Swap the fragments to update the UI based on the item selected.
            val args: Bundle
            when (menuItem.itemId) {
                R.id.nav_rest_calls -> {
                    args = Bundle()
                    args.putLong(RequestFragment.KEY_REQUEST_ID, requestId)
                    replaceFragment(RestCallsFragment.TAG, args)
                }
                R.id.nav_settings -> replaceFragment(SettingsPreferenceFragment.TAG)
                R.id.nav_about -> replaceFragment(AboutFragment.TAG)
                R.id.nav_history -> {
                    args = Bundle()
                    args.putInt(RequestsListFragment.KEY_REQUESTS_LIST_TYPE, RequestsListFragment.LIST_REQUESTS_HISTORY)
                    replaceFragment(RequestsListFragment.TAG, args)
                }
                R.id.nav_saved -> {
                    args = Bundle()
                    args.putInt(RequestsListFragment.KEY_REQUESTS_LIST_TYPE, RequestsListFragment.LIST_SAVED_REQUESTS)
                    replaceFragment(RequestsListFragment.TAG, args)
                }
                else -> throw IllegalArgumentException(TAG)
            }

            return@setNavigationItemSelectedListener true
        }

        // Select the Rest Calls menu item in the navigation menu by default.
        binding.navigationView.menu.performIdentifierAction(R.id.nav_rest_calls, 0)
    }

    /**
     * Replaces the fragment based on the Fragment tag passed in.
     *
     * @param fragmentTag The tag of the fragment to be replaced.
     * @param args        The arguments to be sent to the fragment being replaced.
     */
    private fun replaceFragment(fragmentTag: String, args: Bundle? = null) {
        // Swap the fragments to update the UI based on the item selected.

        val fragment: Fragment = when (fragmentTag) {
            RestCallsFragment.TAG -> RestCallsFragment.newInstance(args)
            SettingsPreferenceFragment.TAG -> SettingsPreferenceFragment.newInstance()
            AboutFragment.TAG -> AboutFragment.newInstance()
            RequestsListFragment.TAG -> RequestsListFragment.newInstance(args)
            else -> throw IllegalArgumentException(TAG)
        }

        supportFragmentManager.beginTransaction().replace(R.id.content_frame, fragment,
                fragment.javaClass.simpleName).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if (binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResponseReceived(restResponse: RestResponse) {
        // Get the reference to RestCallsFragment and call method to switch to the Response Screen tab,
        // and pass the response information.
        val restCallsFragment = supportFragmentManager.findFragmentById(R.id.content_frame) as
                RestCallsFragment?
        restCallsFragment?.switchToResponseScreenTab(restResponse)
    }

    override fun onBackPressed() {
        displayConfirmationDialog()
    }

    /**
     * Display a confirmation dialog to the user when user presses back button to exit the app.
     */
    private fun displayConfirmationDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.confirmation_dialog_title)
                .setMessage(R.string.confirmation_dialog_message)
                .setPositiveButton(android.R.string.yes) {
                    dialog: DialogInterface?, which: Int -> super@HomeActivity.onBackPressed()
                }
                .setNegativeButton(android.R.string.no, null)
                .show()
    }

    override fun onRequestClicked(requestId: Long) {
        this.requestId = requestId
        binding.navigationView.menu.performIdentifierAction(R.id.nav_rest_calls, 0)
    }
}