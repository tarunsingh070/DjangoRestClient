/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import tarun.djangorestclient.com.djangorestclient.R;
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

    @Override
    public HeadersRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.header_row_layout, parent, false);
        return new HeadersRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HeadersRecyclerViewAdapter.ViewHolder holder, final int position) {
        holder.header = headers.get(position);
        holder.tvHeaderName.setText(getHeaderName(holder.header));
        holder.tvHeaderValue.setText(holder.header.getHeaderValue());
        holder.ibDeleteHeader.setOnClickListener(holder);
        holder.ibEditHeader.setOnClickListener(holder);
    }

    /**
     * Return the header name based on the type of header.
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final TextView tvHeaderName;
        final TextView tvHeaderValue;
        final ImageButton ibDeleteHeader;
        final ImageButton ibEditHeader;
        final View mView;
        Header header;

        ViewHolder(View view) {
            super(view);
            mView = view;
            tvHeaderName = view.findViewById(R.id.tv_header_name);
            tvHeaderName.setSelected(true);
            tvHeaderValue = view.findViewById(R.id.tv_header_value);
            ibDeleteHeader = view.findViewById(R.id.ib_delete_header);
            ibEditHeader = view.findViewById(R.id.ib_edit_header);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_delete_header:
                    headerOptionsClickedListener.onDeleteHeaderClicked(headers.indexOf(header));
                    break;
                case R.id.ib_edit_header:
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
