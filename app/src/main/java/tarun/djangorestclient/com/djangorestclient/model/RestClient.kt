/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */
package tarun.djangorestclient.com.djangorestclient.model

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.EMPTY_REQUEST
import tarun.djangorestclient.com.djangorestclient.R
import tarun.djangorestclient.com.djangorestclient.extensions.isNumber
import tarun.djangorestclient.com.djangorestclient.model.entity.Header
import tarun.djangorestclient.com.djangorestclient.utils.HttpUtil
import java.util.concurrent.TimeUnit

/**
 * This network utility class contains methods to make all supported types of rest requests.
 */
class RestClient(private val context: Context?) {
    private val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
    private var client = OkHttpClient.Builder().build()

    /**
     * Update the OkHttpClientConfigurations with the lates shared preference values.
     */
    private fun updateClient() {
        // Fetch the timeout values from shared preferences.
        val timeoutConnectPrefValueString = sharedPreferences.getString(context?.getString(R.string.key_timeout_connect_preference), "")
        val timeoutReadPrefValueString = sharedPreferences.getString(context?.getString(R.string.key_timeout_read_preference), "")
        val timeoutWritePrefValueString = sharedPreferences.getString(context?.getString(R.string.key_timeout_write_preference), "")

        // Check if shared preference values for each preference has been explicitly set by user or not.
        // Also verify if the value set is actually a valid number or not.
        val isTimeOutConnectPrefSet = timeoutConnectPrefValueString?.run { isNotEmpty() && isNumber() } ?: false
        val isTimeOutReadPrefSet = timeoutReadPrefValueString?.run { isNotEmpty() && isNumber() } ?: false
        val isTimeOutWritePrefSet = timeoutWritePrefValueString?.run { isNotEmpty() && isNumber() } ?: false

        val builder = client.newBuilder()

        // Re-configure the builder based on preference values set (if any) in the settings.
        if (isTimeOutConnectPrefSet) {
            builder.connectTimeout(timeoutConnectPrefValueString!!.toInt().toLong(), TimeUnit.SECONDS)
        }

        if (isTimeOutReadPrefSet) {
            builder.readTimeout(timeoutReadPrefValueString!!.toInt().toLong(), TimeUnit.SECONDS)
        }

        if (isTimeOutWritePrefSet) {
            builder.writeTimeout(timeoutWritePrefValueString!!.toInt().toLong(), TimeUnit.SECONDS)
        }

        client = builder.build()
    }

    operator fun get(url: String, headers: List<Header>, callback: Callback) {
        val request = Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers)).get()
                .build()
        enqueueRequest(request, callback)
    }

    fun post(url: String, headers: List<Header>, body: String?, callback: Callback) {
        val request = Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers))
                .post(body?.toRequestBody(null) ?: EMPTY_REQUEST).build()
        enqueueRequest(request, callback)
    }

    fun head(url: String, headers: List<Header>, callback: Callback) {
        val request = Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers))
                .head().build()
        enqueueRequest(request, callback)
    }

    fun put(url: String, headers: List<Header>, body: String?, callback: Callback) {
        val request = Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers))
                .put(body?.toRequestBody(null) ?: EMPTY_REQUEST).build()
        enqueueRequest(request, callback)
    }

    fun delete(url: String, headers: List<Header>, body: String?, callback: Callback) {
        val request = Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers))
                .delete(body?.toRequestBody(null)).build()
        enqueueRequest(request, callback)
    }

    fun patch(url: String, headers: List<Header>, body: String?, callback: Callback) {
        val request = Request.Builder().url(url).headers(HttpUtil.getParsedHeaders(headers))
                .patch(body?.toRequestBody(null) ?: EMPTY_REQUEST).build()
        enqueueRequest(request, callback)
    }

    /**
     * Enqueues the request to be sent and sets the callback to return the response to.
     *
     * @param request  The request to be sent.
     * @param callback The callback to return the response to.
     */
    private fun enqueueRequest(request: Request, callback: Callback) {
        updateClient()
        val call = client.newCall(request)
        call.enqueue(callback)
    }
}