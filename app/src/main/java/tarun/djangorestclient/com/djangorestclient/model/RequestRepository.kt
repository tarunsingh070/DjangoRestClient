/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, June 2020.
 */
package tarun.djangorestclient.com.djangorestclient.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.DataSource
import tarun.djangorestclient.com.djangorestclient.model.entity.Header
import tarun.djangorestclient.com.djangorestclient.model.entity.Request
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders

class RequestRepository(application: Application) {
    private val requestDao: RequestDao
    private var requestsList: DataSource.Factory<Int?, RequestWithHeaders?>? = null

    // FixMe: Note that in order to unit test the RequestRepository, you have to remove the Application
    //  dependency. This adds complexity and much more code, and this sample is not about testing.
    //  See the BasicSample in the android-architecture-components repository at
    //  https://github.com/googlesamples
    init {
        val database = RequestRoomDatabase.getDatabase(application)
        requestDao = database.requestDao()
    }

    /**
     * Gets a request by ID.
     *
     * @param requestId The request ID of the request to fetch.
     * @return A LiveData instance of [RequestWithHeaders]
     */
    fun getRequestById(requestId: Long): LiveData<RequestWithHeaders?> {
        // We want this live data object to emit value only when this particular instance has changed
        // instead of emitting every time some data changes in the whole RequestWithHeaders room DB table
        // which is what happens by default.
        return Transformations.distinctUntilChanged(requestDao.getRequestById(requestId))
    }

    /**
     * Gets the list of all requests in history.
     *
     * @return A DataSource.Factory instance containing the list of [RequestWithHeaders] in history.
     */
    val requestsHistoryList: DataSource.Factory<Int?, RequestWithHeaders?>?
        get() {
            if (requestsList == null) {
                requestsList = requestDao.getRequestsInHistorySortedByDate()
            }
            return requestsList
        }

    /**
     * Gets the list of all saved requests.
     *
     * @return A DataSource.Factory instance containing the list of saved [RequestWithHeaders].
     */
    val savedRequestsList: DataSource.Factory<Int?, RequestWithHeaders?>?
        get() {
            if (requestsList == null) {
                requestsList = requestDao.getSavedRequestsSortedByDate()
            }
            return requestsList
        }

    /**
     * Search for requests in history based on the URL of the requests.
     *
     * @param searchUrlText The URL search term.
     * @return A DataSource.Factory instance containing the list of matching [RequestWithHeaders].
     */
    fun searchRequestsHistoryList(searchUrlText: String?): DataSource.Factory<Int?, RequestWithHeaders?>? {
        return requestDao.searchRequestsInHistorySortedByDate(searchUrlText)
    }

    /**
     * Search for the saved requests based on the URL of the requests.
     *
     * @param searchUrlText The URL search term.
     * @return A DataSource.Factory instance containing the list of matching [RequestWithHeaders].
     */
    fun searchSavedRequestsList(searchUrlText: String?): DataSource.Factory<Int?, RequestWithHeaders?>? {
        return requestDao.searchSavedRequestsSortedByDate(searchUrlText)
    }

    /**
     * Inserts a [Request] object into the Room DB.
     *
     * @param request The instance of [Request] to insert into DB.
     */
    fun insertRequest(request: Request?) {
        // You must call this on a non-UI thread or your app will throw an exception. Room ensures
        // that you're not doing any long running operations on the main thread, blocking the UI.
        RequestRoomDatabase.databaseWriteExecutor.execute { requestDao.insertRequestWithHeaders(request!!) }
    }

    /**
     * Updates an existing [Request] object into the Room DB along with it's associated list
     * of [Header].
     *
     * @param updatedRequest  The updated [Request] object to save.
     * @param existingHeaders The list of updated [Header] to save in DB.
     */
    fun update(updatedRequest: Request, existingHeaders: List<Header>) {
        RequestRoomDatabase.databaseWriteExecutor.execute {
            val headersToInsert: MutableList<Header> = ArrayList()
            val headersToUpdate: MutableList<Header?> = ArrayList()
            val headersToDelete: MutableList<Header?> = ArrayList()
            for (header in updatedRequest.headers) {
                header.parentRequestId = updatedRequest.requestId
                if (header.headerId > 0) {
                    headersToUpdate.add(header)
                } else {
                    headersToInsert.add(header)
                }
            }
            /**
             * FixMe : The following logic is horrible. Find something better.
             */
            outer@ for (existingHeader in existingHeaders) {
                for (updatedHeader in headersToUpdate) {
                    if (existingHeader.headerId == updatedHeader!!.headerId) {
                        continue@outer
                    }
                }
                headersToDelete.add(existingHeader)
            }
            //            existingHeaders.removeAll(headersToUpdate);
//            headersToDelete = existingHeaders;
            requestDao.updateRequestWithHeaders(updatedRequest, headersToInsert, headersToUpdate, headersToDelete)
        }
    }

    /**
     * Deletes all the requests from history.
     */
    fun deleteAllRequestsFromHistory() {
        RequestRoomDatabase.databaseWriteExecutor.execute { requestDao.deleteAllRequestsAndHeadersFromHistory() }
    }

    /**
     * Deletes all the saved requests.
     */
    fun deleteAllSavedRequests() {
        RequestRoomDatabase.databaseWriteExecutor.execute { requestDao.deleteAllSavedRequestsAndHeaders() }
    }

    /**
     * Delete a request by Id.
     *
     * @param requestId The ID of the request to be deleted from the DB.
     */
    fun deleteRequestById(requestId: Long) {
        RequestRoomDatabase.databaseWriteExecutor.execute { requestDao.deleteRequestById(requestId) }
    }
}