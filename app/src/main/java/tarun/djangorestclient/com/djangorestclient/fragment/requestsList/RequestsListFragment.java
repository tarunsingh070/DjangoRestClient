package tarun.djangorestclient.com.djangorestclient.fragment.requestsList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.databinding.FragmentRequestsListBinding;
import tarun.djangorestclient.com.djangorestclient.fragment.DjangoViewModelFactory;
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestsListFragment extends Fragment {

    public static final int LIST_REQUESTS_HISTORY = 1001;
    public static final int LIST_SAVED_REQUESTS = 1002;

    private static final String KEY_REQUESTS_LIST_TYPE = "requests_list_type";

    private int requestsListToShow;
    private RequestsListViewModel requestsListViewModel;
    private FragmentRequestsListBinding binding;

    public RequestsListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment RequestsListFragment.
     */
    public static RequestsListFragment newInstance(int requestsListToShow) {
        RequestsListFragment fragment = new RequestsListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_REQUESTS_LIST_TYPE, requestsListToShow);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            requestsListToShow = getArguments().getInt(KEY_REQUESTS_LIST_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRequestsListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RequestListAdapter adapter = new RequestListAdapter(requireContext());
        binding.requestsListRecyclerView.setAdapter(adapter);
        binding.requestsListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.requestsListRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        requestsListViewModel = new ViewModelProvider(this,
                new DjangoViewModelFactory(requireActivity().getApplication(), requestsListToShow))
                .get(RequestsListViewModel.class);

        binding.tvEmptyLabel.setText(requestsListToShow == LIST_REQUESTS_HISTORY ?
                R.string.no_requests_history : R.string.no_requests_saved);

        requestsListViewModel.getAllrequests().observe(requireActivity(), new Observer<List<RequestWithHeaders>>() {
            @Override
            public void onChanged(List<RequestWithHeaders> requests) {
                adapter.setRequests(requests);
                binding.tvEmptyLabel.setVisibility(requests.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }
}