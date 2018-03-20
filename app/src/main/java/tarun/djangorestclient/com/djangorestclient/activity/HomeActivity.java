package tarun.djangorestclient.com.djangorestclient.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.fragment.AboutFragment;
import tarun.djangorestclient.com.djangorestclient.fragment.RequestFragment;
import tarun.djangorestclient.com.djangorestclient.fragment.RestCallsFragment;
import tarun.djangorestclient.com.djangorestclient.fragment.SettingsPreferenceFragment;
import tarun.djangorestclient.com.djangorestclient.model.RestResponse;

/**
 * This is the home activity which would allow user to navigate to other screens through its navigation drawer.
 */
public class HomeActivity extends AppCompatActivity implements RequestFragment.OnResponseReceivedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        initViews();
        setupViews();
    }

    /**
     * Initialize all views.
     */
    private void initViews() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
    }

    /**
     * Setup all initialized views.
     */
    private void setupViews() {
        setupDrawerLayout();
        setupNavigationView();
    }

    /**
     * Define all actions related to drawer layout here.
     */
    private void setupDrawerLayout() {
        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );
    }

    /**
     * Setup all navigation view related actions here.
     */
    private void setupNavigationView() {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        HomeActivity.this.setTitle(menuItem.getTitle());
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Swap the fragments to update the UI based on the item selected.

                        Fragment fragment;

                        switch (menuItem.getItemId()) {
                            case R.id.nav_rest_calls :
                                fragment = RestCallsFragment.newInstance();
                                break;
                            case R.id.nav_settings :
                                fragment = new SettingsPreferenceFragment();
                                break;
                            case R.id.nav_about :
                                fragment = new AboutFragment();
                                break;
                            default :
                                throw new IllegalArgumentException(TAG);
                        }

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

                        return true;
                    }
                });

        // Select the Rest Calls menu item in the navigation menu by default.
        navigationView.getMenu().performIdentifierAction(R.id.nav_rest_calls, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerLayout.isDrawerOpen(navigationView)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
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
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        HomeActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
