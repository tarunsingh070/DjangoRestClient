/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */
package tarun.djangorestclient.com.djangorestclient.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tarun.djangorestclient.com.djangorestclient.adapter.RequestResponsePagerAdapter
import tarun.djangorestclient.com.djangorestclient.databinding.FragmentRestCallsBinding
import tarun.djangorestclient.com.djangorestclient.fragment.ResponseFragment.Companion.newInstance
import tarun.djangorestclient.com.djangorestclient.model.RestResponse

/**
 * This fragment shows the Request and Response screens in a tabular fashion for the user to make
 * requests and view responses.
 */
class RestCallsFragment : Fragment() {
    companion object {
        const val TAG = "RestCallsFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment RestCallsFragment.
         */
        fun newInstance(args: Bundle?): RestCallsFragment {
            val fragment = RestCallsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var requestFragment: RequestFragment
    private lateinit var responseFragment: ResponseFragment
    private lateinit var binding: FragmentRestCallsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentRestCallsBinding.inflate(inflater, container, false)
        requestFragment = RequestFragment.newInstance(arguments)
        responseFragment = newInstance()

        // Pass in the childFragmentManager instead of the fragmentManager because here we have a
        // view pager based fragment showing nested fragments into it.
        val requestResponsePagerAdapter = RequestResponsePagerAdapter(childFragmentManager,
                requestFragment, responseFragment)
        binding.restViewPager.adapter = requestResponsePagerAdapter

        // Give the TabLayout the ViewPager
        binding.tabLayout.setupWithViewPager(binding.restViewPager)

        return binding.root
    }

    /**
     * Switch to response screen tab and update the Response screen UI with info present in restResponse object.
     *
     * @param restResponse: RestResponse object containing Response information from RequestScreen.
     */
    fun switchToResponseScreenTab(restResponse: RestResponse?) {
        binding.tabLayout.getTabAt(1)!!.select()
        responseFragment.updateUI(restResponse!!)
    }
}