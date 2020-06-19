/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.databinding.HeaderRowLayoutBinding;
import tarun.djangorestclient.com.djangorestclient.fragment.RequestFragment;
import tarun.djangorestclient.com.djangorestclient.model.CustomHeader;
import tarun.djangorestclient.com.djangorestclient.model.Header;

/**
 * The adapter to show the list of all headers.
 */

public class HeadersRecyclerViewAdapter extends RecyclerView.Adapter<HeadersRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<Header> headers;
    private final HeaderOptionsClickedListener headerOptionsClickedListener;

    public HeadersRecyclerViewAdapter(RequestFragment requestFragment, ArrayList<Header> headers) {
        this.headerOptionsClickedListener = requestFragment;
        this.headers = headers;
    }

    @NonNull
    @Override
    public HeadersRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HeaderRowLayoutBinding headerRowLayoutBinding =
                HeaderRowLayoutBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new HeadersRecyclerViewAdapter.ViewHolder(headerRowLayoutBinding);
    }

    @Override
    public void onBindViewHolder(final HeadersRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.header = headers.get(position);
        holder.binding.tvHeaderName.setText(getHeaderName(holder.header));
        holder.binding.tvHeaderValue.setText(holder.header.getHeaderValue());
        holder.binding.deleteHeaderButton.setOnClickListener(holder);
        holder.binding.editHeaderButton.setOnClickListener(holder);
    }

    /**
     * Return the header name based on the type of header.
     *
     * @param header : Header for whom the name is to be returned.
     */
    private String getHeaderName(Header header) {
        if (header.getHeaderType() == Header.HeaderType.CUSTOM) {
            return ((CustomHeader) header).getCustomHeaderType();
        } else {
            return header.getHeaderType().toString();
        }
    }

    @Override
    public int getItemCount() {
        return headers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final HeaderRowLayoutBinding binding;
        Header header;

        ViewHolder(HeaderRowLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.tvHeaderName.setSelected(true);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.delete_header_button:
                    headerOptionsClickedListener.onDeleteHeaderClicked(headers.indexOf(header));
                    break;
                case R.id.edit_header_button:
                    headerOptionsClickedListener.onEditHeaderClicked(headers.indexOf(header));
                    break;
            }
        }
    }

    /**
     * Listener Interface to listen for Edit header and Delete header events.
     */
    public interface HeaderOptionsClickedListener {
        void onDeleteHeaderClicked(int position);

        void onEditHeaderClicked(int position);
    }

}
