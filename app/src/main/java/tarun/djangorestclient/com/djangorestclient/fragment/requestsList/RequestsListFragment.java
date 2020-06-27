package tarun.djangorestclient.com.djangorestclient.fragment.requestsList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.adapter.HeadersRecyclerViewAdapter;
import tarun.djangorestclient.com.djangorestclient.databinding.BottomSheetRequestInfoBinding;
import tarun.djangorestclient.com.djangorestclient.databinding.FragmentRequestsListBinding;
import tarun.djangorestclient.com.djangorestclient.fragment.DjangoViewModelFactory;
import tarun.djangorestclient.com.djangorestclient.model.entity.Header;
import tarun.djangorestclient.com.djangorestclient.model.entity.Request;
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders;

/**
 * The fragment for showing a list of Requests.
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
    private RequestListAdapter adapter;
    private Snackbar requestDeletedSnackbar;
    private RequestsListFragmentListener listener;
    private LiveData<PagedList<RequestWithHeaders>> requestsList;

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
     * Factory method to get an instance of {@link RequestsListFragment}.
     *
     * @param args The arguments to be passed to this fragment.
     * @return A new instance of fragment {@link RequestsListFragment}.
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

        observeRequestsList(requestsListViewModel.getAllrequests());
    }

    /**
     * This method starts observing a list of Requests.
     *
     * @param updatedRequestsList The list of Requests to be observed.
     */
    private void observeRequestsList(LiveData<PagedList<RequestWithHeaders>> updatedRequestsList) {
        if (requestsList != null) {
            requestsList.removeObservers(getViewLifecycleOwner());
        }
        requestsList = updatedRequestsList;
        requestsList.observe(getViewLifecycleOwner(), requests -> {
            adapter.submitList(requests);
            adapter.notifyDataSetChanged();
            binding.tvEmptyLabel.setVisibility(requests.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    /**
     * Sets up the Requests list recycler View.
     *
     * @param adapter The adapter to be attached to the recycler view.
     */
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

    /**
     * Deletes a request from the Requests list at the position specified.
     *
     * @param position The position from which the request is to be deleted.
     */
    void deleteRequestFromList(int position) {
        RequestWithHeaders requestToDelete = adapter.getRequestAtPosition(position);

        //FixMe: Currently, We have the following problem :
        // 1. Delete row "request-2" at position 2, snackbar with undo option will pop up. This will just remove row "request-2" from
        // the adapter's list UI but the underlying PagedList still has "request-2" at index 2 because the request hasn't been deleted from db yet
        // and hence the PagedList hasn't been updated.
        // 2. Now, before the undo snackbar goes down, delete the row right under it i.e. "request-3" which has now shifted to position 2 from position 3.
        // So, the swipe action will actually still give object "request-2" from the PagedList located at index 2 instead of "request-3" which is still
        // located at index 3 even though in UI "request-3" looks in position 2.
        // 3. Because of this another call to delete "request-2" is made instead of "request-3".
        // -
        // Look for a solution, or remove the undo option until then.
//        if (requestDeletedSnackbar != null && requestDeletedSnackbar.isShown()) {
//            requestDeletedSnackbar.dismiss();
//        }
//
//        Snackbar.Callback snackbarCallback = new Snackbar.Callback() {
//            @Override
//            public void onDismissed(Snackbar transientBottomBar, int event) {
//                super.onDismissed(transientBottomBar, event);
//                Toast.makeText(getContext(), "Deleting : " + requestToDelete.getRequest().getUrl(), Toast.LENGTH_SHORT).show();
//                Log.e("bla", "onDismissed: " + "Deleting : " + requestToDelete.getRequest().getUrl());
//                requestsListViewModel
//                        .deleteRequestById(requestToDelete.getRequest().getRequestId());
//            }
//        };
//
//        requestDeletedSnackbar = Snackbar
//                .make(requireView(), R.string.request_deleted, Snackbar.LENGTH_SHORT)
//                .addCallback(snackbarCallback)
//                .setAction(R.string.undo, view -> {
//                    requestDeletedSnackbar.removeCallback(snackbarCallback);
//                    restoreRequestIntoList(requestToDelete, position);
//                });
//
//        requestDeletedSnackbar.show();

        requestsListViewModel
                .deleteRequestById(requestToDelete.getRequest().getRequestId());
    }

    /**
     * Restores a request back into the Requests list at the position specified.
     *
     * @param deletedRequest The instance of the deleted {@link RequestWithHeaders} which needs to be restored.
     * @param position       The position at which to restore the deleted {@link RequestWithHeaders}.
     */
    void restoreRequestIntoList(RequestWithHeaders deletedRequest, int position) {
        adapter.insertRequest(position);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.requests_list_fragment_menu, menu);

        MenuItem searchViewMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchViewMenuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_request_url));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    observeRequestsList(requestsListViewModel.searchRequestsByUrl(query));
                    binding.tvEmptyLabel.setText(R.string.no_results);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty()) {
                    observeRequestsList(requestsListViewModel.getAllrequests());
                    binding.tvEmptyLabel.setText(requestsListToShow == LIST_REQUESTS_HISTORY ?
                            R.string.no_requests_history : R.string.no_requests_saved);
                }
                return false;
            }
        });

        searchViewMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                observeRequestsList(requestsListViewModel.getAllrequests());
                binding.tvEmptyLabel.setText(requestsListToShow == LIST_REQUESTS_HISTORY ?
                        R.string.no_requests_history : R.string.no_requests_saved);
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_requests:
                showDeleteAllConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Shows the confirmation dialog when user tries to delete all the requests.
     */
    void showDeleteAllConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_requests_dialog_title)
                .setMessage(requestsListToShow == LIST_REQUESTS_HISTORY ?
                        R.string.delete_requests_history : R.string.delete_saved_requests)
                .setPositiveButton(R.string.accept, (dialog, which) -> requestsListViewModel.deleteAllRequests())
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    @Override
    public void onRequestClicked(long requestId) {
        listener.onRequestClicked(requestId);
    }

    @Override
    public void onRequestInfoButtonClicked(RequestWithHeaders requestWithHeaders) {
        showAdditionalRequestInfo(requestWithHeaders);
    }

    /**
     * Show the additional response information inside a bottom sheet dialog.
     */
    private void showAdditionalRequestInfo(RequestWithHeaders requestWithHeaders) {
        BottomSheetRequestInfoBinding requestInfoBinding =
                BottomSheetRequestInfoBinding.inflate(getLayoutInflater());

        Request request = requestWithHeaders.getRequest();
        requestInfoBinding.tvRequestUrl.setText(request.getUrl());
        requestInfoBinding.tvRequestType.setText(request.getRequestType().name());

        if (!requestWithHeaders.getHeaders().isEmpty()) {
            setupHeadersRecyclerView(requestInfoBinding.headersRecyclerView, requestWithHeaders.getHeaders());
        } else {
            requestInfoBinding.requestHeadersContainer.setVisibility(View.GONE);
        }

        if (request.getBody() != null && !request.getBody().isEmpty()) {
            requestInfoBinding.tvRequestBody.setText(request.getBody());
        } else {
            requestInfoBinding.requestBodyContainer.setVisibility(View.GONE);
        }

        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(requestInfoBinding.getRoot());
        dialog.show();
    }

    /**
     * Sets up the recycler view for showing the list of headers inside the preview bottom up sheet.
     *
     * @param headersRecyclerView The instance of recycler view to be setup.
     * @param headers             The list of headers to be shown.
     */
    private void setupHeadersRecyclerView(RecyclerView headersRecyclerView, List<Header> headers) {
        HeadersRecyclerViewAdapter headersRecyclerViewAdapter = new HeadersRecyclerViewAdapter(true,
                new ArrayList<>(headers));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        headersRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                linearLayoutManager.getOrientation());
        headersRecyclerView.addItemDecoration(dividerItemDecoration);
        headersRecyclerView.setAdapter(headersRecyclerViewAdapter);
    }
}