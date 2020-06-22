package tarun.djangorestclient.com.djangorestclient.fragment.requestsList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.databinding.FragmentRequestsListBinding;
import tarun.djangorestclient.com.djangorestclient.fragment.DjangoViewModelFactory;
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestsListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestsListFragment extends Fragment implements
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener,
        RequestListAdapter.RequestListAdapterListener {
    public static final String TAG = "RequestsListFragment";

    public static final int LIST_REQUESTS_HISTORY = 1001;
    public static final int LIST_SAVED_REQUESTS = 1002;

    public static final String KEY_REQUESTS_LIST_TYPE = "key_requests_list_type";

    private int requestsListToShow;
    private RequestsListViewModel requestsListViewModel;
    private FragmentRequestsListBinding binding;
    private Snackbar requestDeletedSnackbar;
    private RequestsListFragmentListener listener;

    public RequestsListFragment() {
        // Required empty public constructor
    }

    public interface RequestsListFragmentListener {
        /**
         * Handles the event when a user selects a request.
         *
         * @param requestId The Id of the request selected
         */
        void onRequestClicked(long requestId);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment RequestsListFragment.
     */
    public static RequestsListFragment newInstance(Bundle args) {
        RequestsListFragment fragment = new RequestsListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RequestsListFragmentListener) {
            listener = (RequestsListFragmentListener) context;
        } else {
            throw new RuntimeException("Activity must implement RequestsListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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

    RequestListAdapter adapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new RequestListAdapter(requireContext(), this);
        setupRecyclerView(adapter);

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

    private void setupRecyclerView(RequestListAdapter adapter) {
        binding.requestsListRecyclerView.setAdapter(adapter);
        binding.requestsListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.requestsListRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.requestsListRecyclerView);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        deleteRequestFromList(position);
        adapter.removeRequest(position);
    }

    void deleteRequestFromList(int position) {
        RequestWithHeaders requestToDelete = adapter.getRequests().get(position);

        //FixMe: Currently, quickly deleting rows results in inconsistent no of rows.
        // Look for a solution, or remove the undo option.
        if (requestDeletedSnackbar != null && requestDeletedSnackbar.isShown()) {
            requestDeletedSnackbar.dismiss();
        }

        Snackbar.Callback snackbarCallback = new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                requestsListViewModel
                        .deleteRequestById(requestToDelete.getRequest().getRequestId());
            }
        };

        requestDeletedSnackbar = Snackbar
                .make(requireView(), R.string.request_deleted, Snackbar.LENGTH_LONG)
                .addCallback(snackbarCallback)
                .setAction(R.string.undo, view -> {
                    requestDeletedSnackbar.removeCallback(snackbarCallback);
                    restoreRequestIntoList(requestToDelete, position);
                });

        requestDeletedSnackbar.show();
    }

    void restoreRequestIntoList(RequestWithHeaders deletedRequest, int position) {
        adapter.insertRequest(deletedRequest, position);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.requests_list_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_requests:
                showDeleteConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_requests_dialog_title)
                .setMessage(requestsListToShow == LIST_REQUESTS_HISTORY ?
                        R.string.delete_requests_history : R.string.delete_saved_requests)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> requestsListViewModel.deleteAllRequests())
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onRequestClicked(long requestId) {
        listener.onRequestClicked(requestId);
    }
}