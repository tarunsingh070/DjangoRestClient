package tarun.djangorestclient.com.djangorestclient.fragment.requestsList;

import android.app.Application;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import tarun.djangorestclient.com.djangorestclient.model.RequestRepository;
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders;

public class RequestsListViewModel extends AndroidViewModel {

    private RequestRepository requestRepository;
    private LiveData<List<RequestWithHeaders>> requests;
    private int requestsListToShow;

    public RequestsListViewModel(Application application, int requestsListToShow) {
        super(application);
        requestRepository = new RequestRepository(application);
        this.requestsListToShow = requestsListToShow;
        if (requestsListToShow == RequestsListFragment.LIST_REQUESTS_HISTORY) {
            requests = requestRepository.getRequestsHistoryList();
        } else {
            requests = requestRepository.getSavedRequestsList();
        }
    }

    LiveData<List<RequestWithHeaders>> getAllrequests() {
        return requests;
    }

    void deleteAllRequests() {
        if (requestsListToShow == RequestsListFragment.LIST_REQUESTS_HISTORY) {
            requestRepository.deleteAllRequestsFromHistory();
        } else {
            requestRepository.deleteAllSavedRequests();
        }

    }

    void deleteRequestById(long requestId) {
        requestRepository.deleteRequestById(requestId);
    }
}
