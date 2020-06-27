/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, June 2020.
 */

package tarun.djangorestclient.com.djangorestclient.model;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import tarun.djangorestclient.com.djangorestclient.model.entity.Header;
import tarun.djangorestclient.com.djangorestclient.model.entity.Request;
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders;

public class RequestRepository {

    private RequestDao requestDao;
    private DataSource.Factory<Integer, RequestWithHeaders> requestsList;

    // FixMe: Note that in order to unit test the RequestRepository, you have to remove the Application
    //  dependency. This adds complexity and much more code, and this sample is not about testing.
    //  See the BasicSample in the android-architecture-components repository at
    //  https://github.com/googlesamples
    public RequestRepository(Application application) {
        RequestRoomDatabase database = RequestRoomDatabase.getDatabase(application);
        requestDao = database.requestDao();
    }

    /**
     * Gets a request by ID.
     *
     * @param requestId The request ID of the request to fetch.
     * @return A LiveData instance of {@link RequestWithHeaders}
     */
    public LiveData<RequestWithHeaders> getRequestById(long requestId) {
        return requestDao.getRequestById(requestId);
    }

    /**
     * Gets the list of all requests in history.
     *
     * @return A DataSource.Factory instance containing the list of {@link RequestWithHeaders} in history.
     */
    public DataSource.Factory<Integer, RequestWithHeaders> getRequestsHistoryList() {
        if (requestsList == null) {
            requestsList = requestDao.getRequestsInHistorySortedByDate();
        }
        return requestsList;
    }

    /**
     * Gets the list of all saved requests.
     *
     * @return A DataSource.Factory instance containing the list of saved {@link RequestWithHeaders}.
     */
    public DataSource.Factory<Integer, RequestWithHeaders> getSavedRequestsList() {
        if (requestsList == null) {
            requestsList = requestDao.getSavedRequestsSortedByDate();
        }
        return requestsList;
    }

    /**
     * Search for requests in history based on the URL of the requests.
     *
     * @param searchUrlText The URL search term.
     * @return A DataSource.Factory instance containing the list of matching {@link RequestWithHeaders}.
     */
    public DataSource.Factory<Integer, RequestWithHeaders> searchRequestsHistoryList(String searchUrlText) {
        return requestDao.searchRequestsInHistorySortedByDate(searchUrlText);
    }

    /**
     * Search for the saved requests based on the URL of the requests.
     *
     * @param searchUrlText The URL search term.
     * @return A DataSource.Factory instance containing the list of matching {@link RequestWithHeaders}.
     */
    public DataSource.Factory<Integer, RequestWithHeaders> searchSavedRequestsList(String searchUrlText) {
        return requestDao.searchSavedRequestsSortedByDate(searchUrlText);
    }

    /**
     * Inserts a {@link Request} object into the Room DB.
     *
     * @param request The instance of {@link Request} to insert into DB.
     */
    public void insertRequest(Request request) {
        // You must call this on a non-UI thread or your app will throw an exception. Room ensures
        // that you're not doing any long running operations on the main thread, blocking the UI.
        RequestRoomDatabase.databaseWriteExecutor.execute(() -> requestDao.insertRequestWithHeaders(request));
    }

    /**
     * Updates an existing {@link Request} object into the Room DB along with it's associated list
     * of {@link Header}.
     *
     * @param updatedRequest  The updated {@link Request} object to save.
     * @param existingHeaders The list of updated {@link Header} to save in DB.
     */
    public void update(Request updatedRequest, List<Header> existingHeaders) {
        RequestRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Header> headersToInsert = new ArrayList<>();
            List<Header> headersToUpdate = new ArrayList<>();
            List<Header> headersToDelete;

            for (Header header : updatedRequest.getHeaders()) {
                header.setParentRequestId(updatedRequest.getRequestId());
                if (header.getHeaderId() > 0) {
                    headersToUpdate.add(header);
                } else {
                    headersToInsert.add(header);
                }
            }

            existingHeaders.removeAll(headersToUpdate);
            headersToDelete = existingHeaders;

            requestDao.updateRequestWithHeaders(updatedRequest, headersToInsert, headersToUpdate, headersToDelete);
        });
    }

    /**
     * Deletes all the requests from history.
     */
    public void deleteAllRequestsFromHistory() {
        RequestRoomDatabase.databaseWriteExecutor.execute(() -> requestDao.deleteAllRequestsAndHeadersFromHistory());
    }

    /**
     * Deletes all the saved requests.
     */
    public void deleteAllSavedRequests() {
        RequestRoomDatabase.databaseWriteExecutor.execute(() -> requestDao.deleteAllSavedRequestsAndHeaders());
    }

    /**
     * Delete a request by Id.
     *
     * @param requestId The ID of the request to be deleted from the DB.
     */
    public void deleteRequestById(long requestId) {
        RequestRoomDatabase.databaseWriteExecutor.execute(() -> requestDao.deleteRequestById(requestId));
    }
}
