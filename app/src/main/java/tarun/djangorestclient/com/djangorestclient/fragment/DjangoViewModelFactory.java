package tarun.djangorestclient.com.djangorestclient.fragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import tarun.djangorestclient.com.djangorestclient.fragment.requestsList.RequestsListViewModel;

public class DjangoViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private int requestsListToShow;


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
