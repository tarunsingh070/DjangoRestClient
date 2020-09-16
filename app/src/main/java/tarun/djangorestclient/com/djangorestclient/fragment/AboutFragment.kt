/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */
package tarun.djangorestclient.com.djangorestclient.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tarun.djangorestclient.com.djangorestclient.databinding.FragmentAboutBinding

/**
 * This fragment displays info about this app and allows the user to rate this app on play store.
 */
class AboutFragment : Fragment() {
    companion object {
        const val TAG = "AboutFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment AboutFragment.
         */
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding = FragmentAboutBinding.inflate(inflater, container, false)
        binding.buttonRateMe.setOnClickListener { view: View? -> showAppInPlayStore() }
        return binding.root
    }

    /**
     * Open this application in the PlayStore app of user's device for rating/reviewing this app.
     */
    private fun showAppInPlayStore() {
        val uri = Uri.parse("market://details?id=" + requireActivity().packageName)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" +
                            requireActivity().packageName)))
        }
    }
}