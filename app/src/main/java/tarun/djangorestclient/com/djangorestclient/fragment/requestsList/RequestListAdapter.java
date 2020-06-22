package tarun.djangorestclient.com.djangorestclient.fragment.requestsList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.databinding.ItemRequestBinding;
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders;

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.RequestViewHolder> {

    class RequestViewHolder extends RecyclerView.ViewHolder {
        private final TextView requestType;
        private final TextView requestUrl;
        private final TextView requestHeadersCount;

        private RequestViewHolder(ItemRequestBinding binding) {
            super(binding.getRoot());
            requestType = binding.tvRequestType;
            requestUrl = binding.tvRequestUrl;
            requestHeadersCount = binding.tvHeadersCount;
        }
    }

    private final Context context;
    private List<RequestWithHeaders> requests;

    RequestListAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemRequestBinding binding = ItemRequestBinding.inflate(inflater, parent, false);
        return new RequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(RequestViewHolder holder, int position) {
        if (requests != null) {
            RequestWithHeaders current = requests.get(position);

            holder.requestType.setText(current.getRequest().getRequestType().name());
            holder.requestUrl.setText(current.getRequest().getUrl());
            holder.requestHeadersCount.setText(String.format(context.getString(R.string.header_count),
                    current.getHeaders().size()));
        }
    }

    void setRequests(List<RequestWithHeaders> requests) {
        this.requests = requests;
        notifyDataSetChanged();
    }

    void removeRequest(int position) {
        requests.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return requests != null ? requests.size() : 0;
    }
}
