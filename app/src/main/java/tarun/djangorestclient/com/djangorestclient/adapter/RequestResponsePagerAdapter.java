/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import tarun.djangorestclient.com.djangorestclient.fragment.RequestFragment;
import tarun.djangorestclient.com.djangorestclient.fragment.ResponseFragment;

/**
 * Adapter class for the viewpager displaying the Request and Response Fragments in adjacent tabs.
 */

public class RequestResponsePagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_TABS = 2;

    private static final String TAG = RequestResponsePagerAdapter.class.getSimpleName();

    private RequestFragment requestFragment;
    private ResponseFragment responseFragment;

    public RequestResponsePagerAdapter(FragmentManager fm, RequestFragment requestFragment, ResponseFragment responseFragment) {
        super(fm);
        this.requestFragment = requestFragment;
        this.responseFragment = responseFragment;
    }

    @Override
    public int getCount() {
        return NUM_TABS;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return requestFragment;
        } else {
            return responseFragment;
        }
    }

    // This determines the title for each tab.
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on TAB position
        if (position == 0) {
            return RequestFragment.TITLE;
        } else {
            return ResponseFragment.TITLE;
        }
    }
}
