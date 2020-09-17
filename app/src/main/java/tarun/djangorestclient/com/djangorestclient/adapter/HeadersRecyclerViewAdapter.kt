/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */
package tarun.djangorestclient.com.djangorestclient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import tarun.djangorestclient.com.djangorestclient.R
import tarun.djangorestclient.com.djangorestclient.databinding.HeaderRowLayoutBinding
import tarun.djangorestclient.com.djangorestclient.model.entity.Header
import java.util.*

/**
 * The adapter to show the list of all headers.
 */
class HeadersRecyclerViewAdapter
/**
 * Constructor.
 *
 * @param isReadOnly Boolean indicating if the headers list will be a read-only list or editable.
 * @param headers    The list of headers to be shown, if any.
 * @param headerOptionsClickedListener   An instance of [HeaderOptionsClickedListener]
 */(private val isReadOnly: Boolean, private val headers: ArrayList<Header>,
    private val headerOptionsClickedListener: HeaderOptionsClickedListener? = null) :
        RecyclerView.Adapter<HeadersRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val headerRowLayoutBinding = HeaderRowLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(headerRowLayoutBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.header = headers[position]
        holder.binding.tvHeaderName.text = getHeaderName(holder.header)
        holder.binding.tvHeaderValue.text = holder.header.headerValue
        if (isReadOnly) {
            holder.binding.deleteHeaderButton.visibility = View.GONE
            holder.binding.editHeaderButton.visibility = View.GONE
            return
        }
        holder.binding.deleteHeaderButton.setOnClickListener(holder)
        holder.binding.editHeaderButton.setOnClickListener(holder)
    }

    /**
     * Return the header name based on the type of header.
     *
     * @param header : Header for whom the name is to be returned.
     */
    private fun getHeaderName(header: Header): String? {
        return header.headerType
    }

    override fun getItemCount(): Int {
        return headers.size
    }

    inner class ViewHolder(val binding: HeaderRowLayoutBinding) :
            RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        init {
            binding.tvHeaderName.isSelected = true
        }

        lateinit var header: Header
        override fun onClick(v: View) {
            when (v.id) {
                R.id.delete_header_button ->
                    headerOptionsClickedListener?.onDeleteHeaderClicked(headers.indexOf(header))
                R.id.edit_header_button ->
                    headerOptionsClickedListener?.onEditHeaderClicked(headers.indexOf(header))
            }
        }
    }

    /**
     * Listener Interface to listen for Edit header and Delete header events.
     */
    interface HeaderOptionsClickedListener {
        /**
         * Handles the event when user clicks the button to delete a header.
         *
         * @param position The position of the header clicked.
         */
        fun onDeleteHeaderClicked(position: Int)

        /**
         * Handles the event when user clicks the button to edit a header.
         *
         * @param position The position of the header clicked.
         */
        fun onEditHeaderClicked(position: Int)
    }
}