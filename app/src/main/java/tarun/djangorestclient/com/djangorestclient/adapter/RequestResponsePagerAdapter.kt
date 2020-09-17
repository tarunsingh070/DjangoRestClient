/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */
package tarun.djangorestclient.com.djangorestclient.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import tarun.djangorestclient.com.djangorestclient.fragment.RequestFragment
import tarun.djangorestclient.com.djangorestclient.fragment.ResponseFragment

/**
 * Adapter class for the viewpager displaying the Request and Response Fragments in adjacent tabs.
 */
class RequestResponsePagerAdapter
/**
 * Constructor.
 *
 * @param fm               An instance of [FragmentManager]
 * @param requestFragment  An instance of [RequestFragment]
 * @param responseFragment An instance of [ResponseFragment]
 */(private val fm: FragmentManager, private val requestFragment: RequestFragment,
    private val responseFragment: ResponseFragment) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    companion object {
        private const val NUM_TABS = 2
    }

    override fun getCount(): Int {
        return NUM_TABS
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            requestFragment
        } else {
            responseFragment
        }
    }

    // This determines the title for each tab.
    override fun getPageTitle(position: Int): CharSequence? {
        // Generate title based on TAB position
        return if (position == 0) {
            RequestFragment.TITLE
        } else {
            ResponseFragment.TITLE
        }
    }
}