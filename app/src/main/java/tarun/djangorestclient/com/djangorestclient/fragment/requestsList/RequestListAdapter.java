package tarun.djangorestclient.com.djangorestclient.fragment.requestsList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import tarun.djangorestclient.com.djangorestclient.databinding.ItemRequestBinding;
import tarun.djangorestclient.com.djangorestclient.model.entity.Header;
import tarun.djangorestclient.com.djangorestclient.model.entity.Request;
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders;
import tarun.djangorestclient.com.djangorestclient.utils.DateFormatHelper;

public class RequestListAdapter extends PagedListAdapter<RequestWithHeaders, RequestListAdapter.RequestViewHolder> {

    public interface RequestListAdapterListener {
        /**
         * Handles the event when a user selects a request.
         *
         * @param requestId The Id of the request selected
         */
        void onRequestClicked(long requestId);

        void onRequestInfoButtonClicked(RequestWithHeaders request);
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        private final TextView requestType;
        private final TextView requestUrl;
        //        private final TextView requestHeadersCount;
        private final TextView requestLastUpdatedTimestamp;
        private final ImageView requestInfo;
        public final RelativeLayout viewBackground;
        public final LinearLayout viewForeground;

        private RequestViewHolder(ItemRequestBinding binding) {
            super(binding.getRoot());
            requestType = binding.tvRequestType;
            requestUrl = binding.tvRequestUrl;
//            requestHeadersCount = binding.tvHeadersCount;
            requestLastUpdatedTimestamp = binding.tvTimestamp;
            requestInfo = binding.ivRequestInfo;
            viewBackground = binding.viewBackground;
            viewForeground = binding.viewForeground;
        }
    }

    private final Context context;
    private RequestListAdapterListener listener;

    RequestListAdapter(Context context, RequestListAdapterListener listener) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.listener = listener;
    }

    private static DiffUtil.ItemCallback<RequestWithHeaders> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<RequestWithHeaders>() {
                // Request details may have changed if reloaded from the database,
                // but ID is fixed.
                @Override
                public boolean areItemsTheSame(RequestWithHeaders oldRequestWithHeaders, RequestWithHeaders newRequestWithHeaders) {
                    return oldRequestWithHeaders.getRequest().getRequestId() == newRequestWithHeaders.getRequest().getRequestId();
                }

                @Override
                public boolean areContentsTheSame(RequestWithHeaders oldRequestWithHeaders,
                                                  RequestWithHeaders newRequestWithHeaders) {
                    Request oldRequest = oldRequestWithHeaders.getRequest();
                    List<Header> oldRequestHeaders = oldRequestWithHeaders.getHeaders();

                    Request newRequest = newRequestWithHeaders.getRequest();
                    List<Header> newRequestHeaders = newRequestWithHeaders.getHeaders();

                    return oldRequest.equals(newRequest) &&
                            oldRequestHeaders.size() == newRequestHeaders.size();
                }
            };

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemRequestBinding binding = ItemRequestBinding.inflate(inflater, parent, false);
        return new RequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RequestViewHolder holder, int position) {
        RequestWithHeaders requestWithHeaders = getItem(position);

        holder.requestType.setText(requestWithHeaders.getRequest().getRequestType().name());
        holder.requestUrl.setText(requestWithHeaders.getRequest().getUrl());
//        holder.requestHeadersCount.setText(String.format(context.getString(R.string.header_count),
//                requestWithHeaders.getHeaders().size()));

        holder.requestLastUpdatedTimestamp.setText(DateFormatHelper.getString(requestWithHeaders.getRequest().getUpdatedAt()));

        holder.requestInfo.setOnClickListener(view -> listener.onRequestInfoButtonClicked(getItem(position)));
        holder.viewForeground.setOnClickListener(view ->
                listener.onRequestClicked(requestWithHeaders.getRequest().getRequestId()));
    }

    public RequestWithHeaders getRequestAtPosition(int position) {
        return getItem(position);
    }

    void removeRequest(int position) {
        notifyItemRemoved(position);
    }

    void insertRequest(int position) {
        notifyItemInserted(position);
    }
}
