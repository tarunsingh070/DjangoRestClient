/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, June 2020.
 */
package tarun.djangorestclient.com.djangorestclient.model

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import tarun.djangorestclient.com.djangorestclient.model.entity.Header
import tarun.djangorestclient.com.djangorestclient.model.entity.Request
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders

@Dao
abstract class RequestDao {
    @Insert
    abstract fun insertRequest(request: Request?): Long

    @Update
    abstract fun updateRequest(request: Request?)

    @Insert
    abstract fun insertHeaders(headers: List<Header>?)

    @Update
    abstract fun updateHeaders(headers: List<Header?>?)

    @Delete
    abstract fun deleteHeaders(headers: List<Header?>?)

    @Transaction
    open fun insertRequestWithHeaders(request: Request) {
        val requestId = insertRequest(request)
        val headers = request.headers
        if (headers.isNotEmpty()) {
            for (header in headers) {
                header.parentRequestId = requestId
            }
            insertHeaders(headers)
        }
    }

    @Transaction
    open fun updateRequestWithHeaders(request: Request?, headersToInsert: List<Header>?,
                                      headersToUpdate: List<Header?>?, headersToDelete: List<Header?>?) {
        updateRequest(request)
        updateHeaders(headersToUpdate)
        insertHeaders(headersToInsert)
        deleteHeaders(headersToDelete)
    }

    @Transaction
    open fun deleteAllRequestsAndHeadersFromHistory() {
        deleteAllRequestsFromHistory()
    }

    @Transaction
    open fun deleteAllSavedRequestsAndHeaders() {
        deleteAllSavedRequests()
    }

    @Query("DELETE FROM request WHERE is_in_history")
    abstract fun deleteAllRequestsFromHistory()

    @Query("DELETE FROM request WHERE is_saved")
    abstract fun deleteAllSavedRequests()

    @Query("DELETE FROM request WHERE requestId = :requestId")
    abstract fun deleteRequestById(requestId: Long)

    @Query("DELETE FROM header WHERE headerId = :headerId")
    abstract fun deleteHeader(headerId: Long)

    @Transaction
    @Query("SELECT * from request WHERE is_in_history ORDER BY updated_at_timestamp DESC")
    abstract fun getRequestsInHistorySortedByDate(): DataSource.Factory<Int?, RequestWithHeaders?>?

    @Transaction
    @Query("SELECT * from request WHERE is_saved ORDER BY updated_at_timestamp DESC")
    abstract fun getSavedRequestsSortedByDate(): DataSource.Factory<Int?, RequestWithHeaders?>?

    @Transaction
    @Query("SELECT * from request WHERE is_in_history AND url LIKE '%' || :searchText || '%' ORDER BY updated_at_timestamp DESC")
    abstract fun searchRequestsInHistorySortedByDate(searchText: String?): DataSource.Factory<Int?, RequestWithHeaders?>?

    @Transaction
    @Query("SELECT * from request WHERE is_saved AND url LIKE '%' || :searchText || '%' ORDER BY updated_at_timestamp DESC")
    abstract fun searchSavedRequestsSortedByDate(searchText: String?): DataSource.Factory<Int?, RequestWithHeaders?>?

    @Transaction
    @Query("SELECT * from request WHERE requestId = :requestId")
    abstract fun getRequestById(requestId: Long): LiveData<RequestWithHeaders?>
}