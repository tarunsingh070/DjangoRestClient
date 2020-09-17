/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, June 2020.
 */

package tarun.djangorestclient.com.djangorestclient.fragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import tarun.djangorestclient.com.djangorestclient.fragment.requestsList.RequestsListViewModel;

/**
 * A generic view model factory class to create an instance of the required view model while passing
 * in one or more arguments as well into the View model.
 */
public class DjangoViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private int requestsListToShow;

    /**
     * Constructor.
     *
     * @param application        An instance of {@link Application}
     * @param requestsListToShow The type of requests list to show.
     */
    public DjangoViewModelFactory(Application application, int requestsListToShow) {
        this.application = application;
        this.requestsListToShow = requestsListToShow;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RequestsListViewModel.class)) {
            return (T) new RequestsListViewModel(application, requestsListToShow);
        }

        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass);
    }
}
