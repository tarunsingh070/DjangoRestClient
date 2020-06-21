/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.activity;

import android.os.Build;
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
public class HomeActivity extends AppCompatActivity implements RequestFragment.OnResponseReceivedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private int selectedNavMenuItemId;
    private ActivityHomeBinding binding;

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
        binding.navigationView.setNavigationItemSelectedListener(
                menuItem -> {
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

                    MiscUtil.hideKeyboard(this, this);

                    // Swap the fragments to update the UI based on the item selected.

                    Fragment fragment;

                    switch (menuItem.getItemId()) {
                        case R.id.nav_rest_calls:
                            fragment = RestCallsFragment.newInstance();
                            break;
                        case R.id.nav_settings:
                            fragment = new SettingsPreferenceFragment();
                            break;
                        case R.id.nav_about:
                            fragment = new AboutFragment();
                            break;
                        case R.id.nav_history:
                            fragment = RequestsListFragment.newInstance(RequestsListFragment.LIST_REQUESTS_HISTORY);
                            break;
                        case R.id.nav_saved:
                            fragment = RequestsListFragment.newInstance(RequestsListFragment.LIST_SAVED_REQUESTS);
                            break;
                        default:
                            throw new IllegalArgumentException(TAG);
                    }

                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName()).commit();

                    return true;
                });

        // Select the Rest Calls menu item in the navigation menu by default.
        binding.navigationView.getMenu().performIdentifierAction(R.id.nav_rest_calls, 0);
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
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(R.string.confirmation_dialog_title)
                .setMessage(R.string.confirmation_dialog_message)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> HomeActivity.super.onBackPressed())
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
