package tarun.djangorestclient.com.djangorestclient.model;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import tarun.djangorestclient.com.djangorestclient.model.entity.Header;
import tarun.djangorestclient.com.djangorestclient.model.entity.Request;
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders;

@Dao
public abstract class RequestDao {

    @Insert
    abstract long insertRequest(Request request);

    @Update
    abstract void updateRequest(Request request);

    @Insert
    abstract void insertHeaders(List<Header> headers);

    @Update
    abstract void updateHeaders(List<Header> headers);

    @Transaction
    void insertRequestWithHeaders(Request request) {
        long requestId = insertRequest(request);

        List<Header> headers = request.getHeaders();
        if (!headers.isEmpty()) {
            for (Header header : headers) {
                header.setParentRequestId(requestId);
            }
            insertHeaders(headers);
        }
    }

    @Transaction
    void updateRequestWithHeaders(Request request, List<Header> headersToInsert, List<Header> headersToUpdate) {
        updateRequest(request);
        updateHeaders(headersToUpdate);
        insertHeaders(headersToInsert);
    }

    @Transaction
    void deleteAllRequestsAndHeadersFromHistory() {
        deleteAllRequestsFromHistory();
    }

    @Transaction
    void deleteAllSavedRequestsAndHeaders() {
        deleteAllSavedRequests();
    }

    @Query("DELETE FROM request WHERE is_in_history")
    abstract void deleteAllRequestsFromHistory();

    @Query("DELETE FROM request WHERE is_saved")
    abstract void deleteAllSavedRequests();

    // You can use just @Delete annotation as well if you pass in the exact object you want deleted
    // i.e. you'' have to create a Request object with the same ID (primary key) and pass it here.
    //    @Delete
    @Query("DELETE FROM request WHERE requestId = :requestId")
    abstract void deleteRequestById(long requestId);

    //    @Delete
    @Query("DELETE FROM header WHERE headerId = :headerId")
    abstract void deleteHeader(long headerId);

    @Transaction
    @Query("SELECT * from request WHERE is_in_history ORDER BY updated_at_timestamp DESC")
    abstract LiveData<List<RequestWithHeaders>> getRequestsInHistorySortedByDate();

    @Transaction
    @Query("SELECT * from request WHERE is_saved ORDER BY updated_at_timestamp DESC")
    abstract LiveData<List<RequestWithHeaders>> getSavedRequestsSortedByDate();

    @Transaction
    @Query("SELECT * from request WHERE requestId = :requestId")
    abstract LiveData<RequestWithHeaders> getRequestById(long requestId);
}
