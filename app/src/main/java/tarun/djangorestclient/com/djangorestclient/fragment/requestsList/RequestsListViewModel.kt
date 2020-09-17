/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, June 2020.
 */
package tarun.djangorestclient.com.djangorestclient.fragment.requestsList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import tarun.djangorestclient.com.djangorestclient.model.RequestRepository
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders

class RequestsListViewModel
/**
 * Constructor.
 *
 * @param application        An instance of [Application]
 * @param requestsListToShow The type of requests list to show.
 */
(application: Application?, private val requestsListToShow: Int) : AndroidViewModel(application!!) {
    companion object {
        private const val REQUESTS_LIST_PAGE_SIZE = 50
    }

    private val requestRepository: RequestRepository = RequestRepository(application!!)
    private lateinit var requests: LiveData<PagedList<RequestWithHeaders>>

    /**
     * Gets the list of all requests to show based on the type of requests to show.
     *
     * @return The [PagedList] of requests to show.
     */
    val allRequests: LiveData<PagedList<RequestWithHeaders>>
        get() {
            requests = if (requestsListToShow == RequestsListFragment.LIST_REQUESTS_HISTORY) {
                LivePagedListBuilder(requestRepository.requestsHistoryList,
                        REQUESTS_LIST_PAGE_SIZE).build()
            } else {
                LivePagedListBuilder(requestRepository.savedRequestsList,
                        REQUESTS_LIST_PAGE_SIZE).build()
            }
            return requests
        }

    /**
     * Search for requests from history/saved list based on the URL of the requests.
     *
     * @param searchUrlText The URL search term.
     * @return The [PagedList] of requests from search results.
     */
    fun searchRequestsByUrl(searchUrlText: String?): LiveData<PagedList<RequestWithHeaders>> {
        requests = if (requestsListToShow == RequestsListFragment.LIST_REQUESTS_HISTORY) {
            LivePagedListBuilder(requestRepository.searchRequestsHistoryList(searchUrlText),
                    REQUESTS_LIST_PAGE_SIZE).build()
        } else {
            LivePagedListBuilder(requestRepository.searchSavedRequestsList(searchUrlText),
                    REQUESTS_LIST_PAGE_SIZE).build()
        }
        return requests
    }

    /**
     * Deletes all the requests from history/saved list.
     */
    fun deleteAllRequests() {
        if (requestsListToShow == RequestsListFragment.LIST_REQUESTS_HISTORY) {
            requestRepository.deleteAllRequestsFromHistory()
        } else {
            requestRepository.deleteAllSavedRequests()
        }
    }

    /**
     * Delete a request by Id.
     *
     * @param requestId The ID of the request to be deleted.
     */
    fun deleteRequestById(requestId: Long?) {
        requestRepository.deleteRequestById(requestId)
    }
}