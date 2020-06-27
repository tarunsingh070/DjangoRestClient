/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, June 2020.
 */

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

    /**
     * Constructor.
     *
     * @param application        An instance of {@link Application}
     * @param requestsListToShow The type of requests list to show.
     */
    public RequestsListViewModel(Application application, int requestsListToShow) {
        super(application);
        requestRepository = new RequestRepository(application);
        this.requestsListToShow = requestsListToShow;
    }

    /**
     * Gets the list of all requests to show based on the type of requests to show.
     *
     * @return The {@link PagedList} of requests to show.
     */
    LiveData<PagedList<RequestWithHeaders>> getAllRequests() {
        if (requestsListToShow == RequestsListFragment.LIST_REQUESTS_HISTORY) {
            requests = new LivePagedListBuilder<>(requestRepository.getRequestsHistoryList(),
                    REQUESTS_LIST_PAGE_SIZE).build();
        } else {
            requests = new LivePagedListBuilder<>(requestRepository.getSavedRequestsList(),
                    REQUESTS_LIST_PAGE_SIZE).build();
        }
        return requests;
    }

    /**
     * Search for requests from history/saved list based on the URL of the requests.
     *
     * @param searchUrlText The URL search term.
     * @return The {@link PagedList} of requests from search results.
     */
    LiveData<PagedList<RequestWithHeaders>> searchRequestsByUrl(String searchUrlText) {
        if (requestsListToShow == RequestsListFragment.LIST_REQUESTS_HISTORY) {
            requests = new LivePagedListBuilder<>(requestRepository.searchRequestsHistoryList(searchUrlText),
                    REQUESTS_LIST_PAGE_SIZE).build();
        } else {
            requests = new LivePagedListBuilder<>(requestRepository.searchSavedRequestsList(searchUrlText),
                    REQUESTS_LIST_PAGE_SIZE).build();
        }
        return requests;
    }

    /**
     * Deletes all the requests from history/saved list.
     */
    void deleteAllRequests() {
        if (requestsListToShow == RequestsListFragment.LIST_REQUESTS_HISTORY) {
            requestRepository.deleteAllRequestsFromHistory();
        } else {
            requestRepository.deleteAllSavedRequests();
        }
    }

    /**
     * Delete a request by Id.
     *
     * @param requestId The ID of the request to be deleted.
     */
    void deleteRequestById(long requestId) {
        requestRepository.deleteRequestById(requestId);
    }
}
