/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.databinding.ActivityHomeBinding;
import tarun.djangorestclient.com.djangorestclient.fragment.AboutFragment;
import tarun.djangorestclient.com.djangorestclient.fragment.RequestFragment;
import tarun.djangorestclient.com.djangorestclient.fragment.RestCallsFragment;
import tarun.djangorestclient.com.djangorestclient.fragment.SettingsPreferenceFragment;
import tarun.djangorestclient.com.djangorestclient.fragment.requestsList.RequestsListFragment;
import tarun.djangorestclient.com.djangorestclient.model.RestResponse;
import tarun.djangorestclient.com.djangorestclient.utils.MiscUtil;

/**
 * This is the home activity which would allow user to navigate to other screens through its navigation drawer.
 */
public class HomeActivity extends AppCompatActivity implements
        RequestFragment.OnResponseReceivedListener, RequestsListFragment.RequestsListFragmentListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private int selectedNavMenuItemId;
    private ActivityHomeBinding binding;
    private long requestId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionbar.setElevation(0);

        setupViews();
    }

    /**
     * Setup all initialized views.
     */
    private void setupViews() {
        setupNavigationView();
    }

    /**
     * Setup all navigation view related actions here.
     */
    private void setupNavigationView() {
        binding.navigationView.setNavigationItemSelectedListener(menuItem -> {
            // close drawer when item is tapped
            binding.drawerLayout.closeDrawers();

            // Simply return if selected menu item was already selected.
            if (menuItem.getItemId() == selectedNavMenuItemId) {
                return true;
            }

            // store the selected menuItemId.
            selectedNavMenuItemId = menuItem.getItemId();

            // set item as selected to persist highlight
            menuItem.setChecked(true);
            HomeActivity.this.setTitle(menuItem.getTitle());

            MiscUtil.hideKeyboard(HomeActivity.this, HomeActivity.this);

            // Swap the fragments to update the UI based on the item selected.

            Bundle args;
            switch (menuItem.getItemId()) {
                case R.id.nav_rest_calls:
                    args = new Bundle();
                    args.putLong(RequestFragment.KEY_REQUEST_ID, requestId);
                    replaceFragment(RestCallsFragment.TAG, args);
                    break;
                case R.id.nav_settings:
                    replaceFragment(SettingsPreferenceFragment.TAG);
                    break;
                case R.id.nav_about:
                    replaceFragment(AboutFragment.TAG);
                    break;
                case R.id.nav_history:
                    args = new Bundle();
                    args.putInt(RequestsListFragment.KEY_REQUESTS_LIST_TYPE, RequestsListFragment.LIST_REQUESTS_HISTORY);
                    replaceFragment(RequestsListFragment.TAG, args);
                    break;
                case R.id.nav_saved:
                    args = new Bundle();
                    args.putInt(RequestsListFragment.KEY_REQUESTS_LIST_TYPE, RequestsListFragment.LIST_SAVED_REQUESTS);
                    replaceFragment(RequestsListFragment.TAG, args);
                    break;
                default:
                    throw new IllegalArgumentException(TAG);
            }

            return true;
        });

        // Select the Rest Calls menu item in the navigation menu by default.
        binding.navigationView.getMenu().performIdentifierAction(R.id.nav_rest_calls, 0);
    }

    private void replaceFragment(final String fragmentTag) {
        replaceFragment(fragmentTag, null);
    }

    private void replaceFragment(final String fragmentTag, Bundle args) {
        // Swap the fragments to update the UI based on the item selected.

        Fragment fragment;

        switch (fragmentTag) {
            case RestCallsFragment.TAG:
                fragment = RestCallsFragment.newInstance(args);
                break;
            case SettingsPreferenceFragment.TAG:
                fragment = SettingsPreferenceFragment.newInstance();
                break;
            case AboutFragment.TAG:
                fragment = AboutFragment.newInstance();
                break;
            case RequestsListFragment.TAG:
                fragment = RequestsListFragment.newInstance(args);
                break;
            default:
                throw new IllegalArgumentException(TAG);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponseReceived(RestResponse restResponse) {
        // Get the reference to RestCallsFragment and call method to switch to the Response Screen tab,
        // and pass the response information.
        RestCallsFragment restCallsFragment = (RestCallsFragment)
                getSupportFragmentManager().findFragmentById(R.id.content_frame);
        restCallsFragment.switchToResponseScreenTab(restResponse);
    }

    @Override
    public void onBackPressed() {
        displayConfirmationDialog();
    }

    /**
     * Display a confirmation dialog to the user when user presses back button to exit the app.
     */
    private void displayConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirmation_dialog_title)
                .setMessage(R.string.confirmation_dialog_message)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> HomeActivity.super.onBackPressed())
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void onRequestClicked(long requestId) {
        this.requestId = requestId;
        binding.navigationView.getMenu().performIdentifierAction(R.id.nav_rest_calls, 0);
    }
}
