package tarun.djangorestclient.com.djangorestclient.model;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import tarun.djangorestclient.com.djangorestclient.model.entity.Header;
import tarun.djangorestclient.com.djangorestclient.model.entity.Request;
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders;

public class RequestRepository {

    private RequestDao requestDao;
    private LiveData<List<RequestWithHeaders>> requestsHistoryList;
    private LiveData<List<RequestWithHeaders>> savedRequestsList;

    // FixMe: Note that in order to unit test the RequestRepository, you have to remove the Application
    //  dependency. This adds complexity and much more code, and this sample is not about testing.
    //  See the BasicSample in the android-architecture-components repository at
    //  https://github.com/googlesamples
    public RequestRepository(Application application) {
        RequestRoomDatabase database = RequestRoomDatabase.getDatabase(application);
        requestDao = database.requestDao();
    }

    public LiveData<RequestWithHeaders> getRequestById(long requestId) {
        return requestDao.getRequestById(requestId);
    }

    public LiveData<List<RequestWithHeaders>> getRequestsHistoryList() {
        if (requestsHistoryList == null) {
            requestsHistoryList = requestDao.getRequestsInHistorySortedByDate();
        }
        return requestsHistoryList;
    }

    public LiveData<List<RequestWithHeaders>> getSavedRequestsList() {
        if (savedRequestsList == null) {
            savedRequestsList = requestDao.getSavedRequestsSortedByDate();
        }
        return savedRequestsList;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(Request request) {
        RequestRoomDatabase.databaseWriteExecutor.execute(() -> {
            requestDao.insertRequestWithHeaders(request);
        });
    }

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

    public void deleteAllRequestsFromHistory() {
        RequestRoomDatabase.databaseWriteExecutor.execute(() -> {
            requestDao.deleteAllRequestsAndHeadersFromHistory();
        });
    }

    public void deleteAllSavedRequests() {
        RequestRoomDatabase.databaseWriteExecutor.execute(() -> {
            requestDao.deleteAllSavedRequestsAndHeaders();
        });
    }

    public void deleteRequestById(long requestId) {
        RequestRoomDatabase.databaseWriteExecutor.execute(() -> {
            requestDao.deleteRequestById(requestId);
        });
    }
}
