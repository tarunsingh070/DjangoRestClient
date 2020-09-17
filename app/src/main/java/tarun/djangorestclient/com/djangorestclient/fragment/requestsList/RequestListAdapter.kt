/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, June 2020.
 */
package tarun.djangorestclient.com.djangorestclient.fragment.requestsList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tarun.djangorestclient.com.djangorestclient.databinding.ItemRequestBinding
import tarun.djangorestclient.com.djangorestclient.fragment.requestsList.RequestListAdapter.RequestListAdapterListener
import tarun.djangorestclient.com.djangorestclient.fragment.requestsList.RequestListAdapter.RequestViewHolder
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders
import tarun.djangorestclient.com.djangorestclient.utils.DateFormatHelper

class RequestListAdapter
/**
 * Constructor.
 *
 * @param context  An instance of [Context]
 * @param listener An instance of [RequestListAdapterListener]
 */(private val context: Context, private val listener: RequestListAdapterListener) :
        PagedListAdapter<RequestWithHeaders, RequestViewHolder>(DIFF_CALLBACK) {
    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<RequestWithHeaders> = object : DiffUtil.ItemCallback<RequestWithHeaders>() {
            // Request details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(oldRequestWithHeaders: RequestWithHeaders, newRequestWithHeaders: RequestWithHeaders): Boolean {
                return oldRequestWithHeaders.request.requestId == newRequestWithHeaders.request.requestId
            }

            override fun areContentsTheSame(oldRequestWithHeaders: RequestWithHeaders,
                                            newRequestWithHeaders: RequestWithHeaders): Boolean {
                val oldRequest = oldRequestWithHeaders.request
                val oldRequestHeaders = oldRequestWithHeaders.headers
                val newRequest = newRequestWithHeaders.request
                val newRequestHeaders = newRequestWithHeaders.headers
                return oldRequest == newRequest &&
                        oldRequestHeaders.size == newRequestHeaders.size
            }
        }
    }

    interface RequestListAdapterListener {
        /**
         * Handles the event when a user selects a request.
         *
         * @param requestId The Id of the request selected
         */
        fun onRequestClicked(requestId: Long?)

        /**
         * Handles the event when a user taps the info button of a request.
         *
         * @param request The request whose info button has been clicked.
         */
        fun onRequestInfoButtonClicked(request: RequestWithHeaders?)
    }

    inner class RequestViewHolder(binding: ItemRequestBinding) : RecyclerView.ViewHolder(binding.root) {
        val requestType: TextView = binding.tvRequestType
        val requestUrl: TextView = binding.tvRequestUrl

        //        val requestHeadersCount: TextView = binding.tvHeadersCount
        val requestLastUpdatedTimestamp: TextView = binding.tvTimestamp
        val requestInfo: ImageView = binding.ivRequestInfo
        val viewBackground: RelativeLayout = binding.viewBackground

        @JvmField
        val viewForeground: LinearLayout = binding.viewForeground
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = ItemRequestBinding.inflate(inflater, parent, false)
        return RequestViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = getItem(position)?.request
        holder.requestType.text = request?.requestType?.name
        holder.requestUrl.text = request?.url
        //        holder.requestHeadersCount.setText(String.format(context.getString(R.string.header_count),
//                requestWithHeaders.getHeaders().size()));
        holder.requestLastUpdatedTimestamp.text = DateFormatHelper.getString(request?.updatedAt)
        holder.requestInfo.setOnClickListener { listener.onRequestInfoButtonClicked(getItem(position)) }
        holder.viewForeground.setOnClickListener { listener.onRequestClicked(request?.requestId) }
    }

    /**
     * Getter for the [RequestWithHeaders] object at the position passed in.
     *
     * @param position The position of the [RequestWithHeaders] object to be retrieved.
     * @return The [RequestWithHeaders] object at the position passed in.
     */
    fun getRequestAtPosition(position: Int): RequestWithHeaders? {
        return getItem(position)
    }

    /**
     * Remove the request row at the position specified.
     *
     * @param position The position from which the Request row is to be deleted.
     */
    fun removeRequest(position: Int) {
        notifyItemRemoved(position)
    }

    /**
     * Insert back the request row at the position specified.
     *
     * @param position The position at which the Request row is to be inserted.
     */
    fun insertRequest(position: Int) {
        notifyItemInserted(position)
    }
}