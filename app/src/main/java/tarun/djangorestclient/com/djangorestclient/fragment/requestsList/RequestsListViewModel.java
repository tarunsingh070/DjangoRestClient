package tarun.djangorestclient.com.djangorestclient.fragment.requestsList;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import tarun.djangorestclient.com.djangorestclient.model.RequestRepository;
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders;

public class RequestsListViewModel extends AndroidViewModel {

    private static final int REQUESTS_LIST_PAGE_SIZE = 50;

    private RequestRepository requestRepository;
    private LiveData<PagedList<RequestWithHeaders>> requests;
    private int requestsListToShow;

    public RequestsListViewModel(Application application, int requestsListToShow) {
        super(application);
        requestRepository = new RequestRepository(application);
        this.requestsListToShow = requestsListToShow;
    }

    LiveData<PagedList<RequestWithHeaders>> getAllrequests() {
        if (requestsListToShow == RequestsListFragment.LIST_REQUESTS_HISTORY) {
            requests = new LivePagedListBuilder<>(requestRepository.getRequestsHistoryList(),
                    REQUESTS_LIST_PAGE_SIZE).build();
        } else {
            requests = new LivePagedListBuilder<>(requestRepository.getSavedRequestsList(),
                    REQUESTS_LIST_PAGE_SIZE).build();
        }
        return requests;
    }

    LiveData<PagedList<RequestWithHeaders>> searchRequestsByUrl(String searchText) {
        if (requestsListToShow == RequestsListFragment.LIST_REQUESTS_HISTORY) {
            requests = new LivePagedListBuilder<>(requestRepository.searchRequestsHistoryList(searchText),
                    REQUESTS_LIST_PAGE_SIZE).build();
        } else {
            requests = new LivePagedListBuilder<>(requestRepository.searchSavedRequestsList(searchText),
                    REQUESTS_LIST_PAGE_SIZE).build();
        }
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
