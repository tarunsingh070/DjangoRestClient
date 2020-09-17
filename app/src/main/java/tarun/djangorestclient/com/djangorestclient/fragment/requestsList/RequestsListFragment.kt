/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, June 2020.
 */
package tarun.djangorestclient.com.djangorestclient.fragment.requestsList

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import tarun.djangorestclient.com.djangorestclient.R
import tarun.djangorestclient.com.djangorestclient.adapter.HeadersRecyclerViewAdapter
import tarun.djangorestclient.com.djangorestclient.databinding.BottomSheetRequestInfoBinding
import tarun.djangorestclient.com.djangorestclient.databinding.FragmentRequestsListBinding
import tarun.djangorestclient.com.djangorestclient.extensions.getFormattedJsonText
import tarun.djangorestclient.com.djangorestclient.fragment.DjangoViewModelFactory
import tarun.djangorestclient.com.djangorestclient.fragment.requestsList.RecyclerItemTouchHelper.RecyclerItemTouchHelperListener
import tarun.djangorestclient.com.djangorestclient.fragment.requestsList.RequestListAdapter.RequestListAdapterListener
import tarun.djangorestclient.com.djangorestclient.model.entity.Header
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders
import tarun.djangorestclient.com.djangorestclient.utils.HttpUtil
import java.util.*

/**
 * The fragment for showing a list of Requests.
 */
class RequestsListFragment : Fragment(), RecyclerItemTouchHelperListener, RequestListAdapterListener {
    companion object {
        const val TAG = "RequestsListFragment"
        const val LIST_REQUESTS_HISTORY = 1001
        const val LIST_SAVED_REQUESTS = 1002
        const val KEY_REQUESTS_LIST_TYPE = "key_requests_list_type"

        /**
         * Factory method to get an instance of [RequestsListFragment].
         *
         * @param args The arguments to be passed to this fragment.
         * @return A new instance of fragment [RequestsListFragment].
         */
        fun newInstance(args: Bundle?): RequestsListFragment {
            val fragment = RequestsListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var requestsListToShow = 0
    private lateinit var requestsListViewModel: RequestsListViewModel
    private lateinit var binding: FragmentRequestsListBinding
    private lateinit var adapter: RequestListAdapter
    private lateinit var requestDeletedSnackbar: Snackbar
    private var listener: RequestsListFragmentListener? = null
    private var requestsList: LiveData<PagedList<RequestWithHeaders>>? = null

    interface RequestsListFragmentListener {
        /**
         * Handles the event when a user selects a request.
         *
         * @param requestId The Id of the request selected
         */
        fun onRequestClicked(requestId: Long?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = if (context is RequestsListFragmentListener) {
            context
        } else {
            throw RuntimeException("Activity must implement RequestsListFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (arguments != null) {
            requestsListToShow = requireArguments().getInt(KEY_REQUESTS_LIST_TYPE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentRequestsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = RequestListAdapter(requireContext(), this)
        setupRecyclerView(adapter)

        requestsListViewModel = ViewModelProvider(this,
                DjangoViewModelFactory(requireActivity().application, requestsListToShow))
                .get(RequestsListViewModel::class.java)

        binding.tvEmptyLabel.setText(if (requestsListToShow == LIST_REQUESTS_HISTORY)
            R.string.no_requests_history else R.string.no_requests_saved)

        observeRequestsList(requestsListViewModel.allRequests)
    }

    /**
     * This method starts observing a list of Requests.
     *
     * @param updatedRequestsList The list of Requests to be observed.
     */
    private fun observeRequestsList(updatedRequestsList: LiveData<PagedList<RequestWithHeaders>>) {
        requestsList?.removeObservers(viewLifecycleOwner)
        requestsList = updatedRequestsList
        requestsList?.observe(viewLifecycleOwner, { requests: PagedList<RequestWithHeaders> ->
            adapter.submitList(requests)
            adapter.notifyDataSetChanged()
            binding.tvEmptyLabel.visibility = if (requests.isEmpty()) View.VISIBLE else View.GONE
        })
    }

    /**
     * Sets up the Requests list recycler View.
     *
     * @param adapter The adapter to be attached to the recycler view.
     */
    private fun setupRecyclerView(adapter: RequestListAdapter) {
        binding.requestsListRecyclerView.adapter = adapter
        binding.requestsListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.requestsListRecyclerView.addItemDecoration(DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL))

        val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback = RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.requestsListRecyclerView)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        deleteRequestFromList(position)
        adapter.removeRequest(position)
    }

    /**
     * Deletes a request from the Requests list at the position specified.
     *
     * @param position The position from which the request is to be deleted.
     */
    private fun deleteRequestFromList(position: Int) {
        val request = adapter.getRequestAtPosition(position)?.request

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

        requestsListViewModel.deleteRequestById(request?.requestId)
    }

    /**
     * Restores a request back into the Requests list at the position specified.
     *
     * @param deletedRequest The instance of the deleted [RequestWithHeaders] which needs to be restored.
     * @param position       The position at which to restore the deleted [RequestWithHeaders].
     */
    fun restoreRequestIntoList(deletedRequest: RequestWithHeaders?, position: Int) {
        adapter.insertRequest(position)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.requests_list_fragment_menu, menu)

        val searchViewMenuItem = menu.findItem(R.id.action_search)
        val searchView = searchViewMenuItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_request_url)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isNotEmpty()) {
                    observeRequestsList(requestsListViewModel.searchRequestsByUrl(query))
                    binding.tvEmptyLabel.setText(R.string.no_results)
                }
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                // FixMe: Search for a request in history and click it, the app crashes. To fix it
                //  "isVisible" flag had to be checked in the below condition, but ideal solution
                //  would be to somehow remove this listener from searchView when onDestroyView() is
                //  called. To reproduce the bug, remove "isVisible" flag and perform the above steps.
                if (s.isEmpty() && isVisible) {
                    observeRequestsList(requestsListViewModel.allRequests)
                    binding.tvEmptyLabel.setText(if (requestsListToShow == LIST_REQUESTS_HISTORY) R.string.no_requests_history else R.string.no_requests_saved)
                }
                return false
            }
        })

        searchViewMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                observeRequestsList(requestsListViewModel.allRequests)
                binding.tvEmptyLabel.setText(if (requestsListToShow == LIST_REQUESTS_HISTORY)
                    R.string.no_requests_history else R.string.no_requests_saved)
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_all_requests -> {
                showDeleteAllConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Shows the confirmation dialog when user tries to delete all the requests.
     */
    private fun showDeleteAllConfirmationDialog() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete_requests_dialog_title)
                .setMessage(if (requestsListToShow == LIST_REQUESTS_HISTORY)
                    R.string.delete_requests_history else R.string.delete_saved_requests)
                .setPositiveButton(R.string.accept) { dialog: DialogInterface?, which: Int -> requestsListViewModel.deleteAllRequests() }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
    }

    override fun onRequestClicked(requestId: Long?) {
        listener?.onRequestClicked(requestId)
    }

    override fun onRequestInfoButtonClicked(requestWithHeaders: RequestWithHeaders?) {
        showAdditionalRequestInfoBottomSheet(requestWithHeaders)
    }

    /**
     * Show the additional response information inside a bottom sheet dialog.
     */
    private fun showAdditionalRequestInfoBottomSheet(requestWithHeaders: RequestWithHeaders?) {
        val requestInfoBinding = BottomSheetRequestInfoBinding.inflate(layoutInflater)
        val request = requestWithHeaders?.request
        requestInfoBinding.tvRequestUrl.text = request?.url
        requestInfoBinding.tvRequestType.text = request?.requestType?.name

        if (requestWithHeaders?.headers != null && requestWithHeaders.headers.isNotEmpty()) {
            setupHeadersRecyclerView(requestInfoBinding.headersRecyclerView, requestWithHeaders.headers)
        } else {
            requestInfoBinding.requestHeadersContainer.visibility = View.GONE
        }

        if (request?.body != null && request.body!!.isNotEmpty()) {
            requestInfoBinding.tvRequestBody.text = request.body!!.getFormattedJsonText()
        } else {
            requestInfoBinding.requestBodyContainer.visibility = View.GONE
        }

        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(requestInfoBinding.root)
        dialog.show()
    }

    /**
     * Sets up the recycler view for showing the list of headers inside the preview bottom up sheet.
     *
     * @param headersRecyclerView The instance of recycler view to be setup.
     * @param headers             The list of headers to be shown.
     */
    private fun setupHeadersRecyclerView(headersRecyclerView: RecyclerView, headers: List<Header>) {
        val headersRecyclerViewAdapter = HeadersRecyclerViewAdapter(true,
                ArrayList(headers))
        val linearLayoutManager = LinearLayoutManager(context)
        headersRecyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(context,
                linearLayoutManager.orientation)
        headersRecyclerView.addItemDecoration(dividerItemDecoration)
        headersRecyclerView.adapter = headersRecyclerViewAdapter
    }
}