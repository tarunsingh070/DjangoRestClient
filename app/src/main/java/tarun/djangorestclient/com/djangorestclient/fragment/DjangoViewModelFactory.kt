/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, June 2020.
 */
package tarun.djangorestclient.com.djangorestclient.fragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tarun.djangorestclient.com.djangorestclient.fragment.requestsList.RequestsListViewModel

/**
 * A generic view model factory class to create an instance of the required view model while passing
 * in one or more arguments as well into the View model.
 */
class DjangoViewModelFactory
/**
 * Constructor.
 *
 * @param application        An instance of [Application]
 * @param requestsListToShow The type of requests list to show.
 */(private val application: Application, private val requestsListToShow: Int) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestsListViewModel::class.java)) {
            return RequestsListViewModel(application, requestsListToShow) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}