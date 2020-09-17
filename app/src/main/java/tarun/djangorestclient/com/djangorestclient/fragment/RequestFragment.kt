/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */
package tarun.djangorestclient.com.djangorestclient.fragment

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Patterns
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers
import okhttp3.Response
import tarun.djangorestclient.com.djangorestclient.R
import tarun.djangorestclient.com.djangorestclient.adapter.HeadersRecyclerViewAdapter
import tarun.djangorestclient.com.djangorestclient.adapter.HeadersRecyclerViewAdapter.HeaderOptionsClickedListener
import tarun.djangorestclient.com.djangorestclient.databinding.DialogAddHeaderBinding
import tarun.djangorestclient.com.djangorestclient.databinding.FragmentRequestBinding
import tarun.djangorestclient.com.djangorestclient.model.RequestRepository
import tarun.djangorestclient.com.djangorestclient.model.RestResponse
import tarun.djangorestclient.com.djangorestclient.model.entity.Header
import tarun.djangorestclient.com.djangorestclient.model.entity.Header.HeaderType
import tarun.djangorestclient.com.djangorestclient.model.entity.Request
import tarun.djangorestclient.com.djangorestclient.model.entity.Request.RequestType
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders
import tarun.djangorestclient.com.djangorestclient.utils.DateFormatHelper
import tarun.djangorestclient.com.djangorestclient.utils.HttpUtil
import tarun.djangorestclient.com.djangorestclient.utils.MiscUtil
import tarun.djangorestclient.com.djangorestclient.model.RestClient
import java.io.IOException

/**
 * This fragment shows user all necessary fields to make REST requests.
 */
class RequestFragment : Fragment(), HeaderOptionsClickedListener {
    companion object {
        const val TITLE = "Request"
        private val TAG = RequestFragment::class.java.simpleName
        private const val NEW_HEADER_POSITION = -1
        const val KEY_REQUEST_ID = "key_request_id"

        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @param args The arguments to be passed into this fragment.
         * @return A new instance of fragment RequestFragment.
         */
        @JvmStatic
        fun newInstance(args: Bundle?): RequestFragment {
            val fragment = RequestFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var headersRecyclerViewAdapter: HeadersRecyclerViewAdapter
    private lateinit var binding: FragmentRequestBinding
    private lateinit var request: Request
    private lateinit var requestWithHeadersLiveData: LiveData<RequestWithHeaders?>
    private lateinit var mListener: OnResponseReceivedListener
    private lateinit var restClient: RestClient
    private lateinit var requestRepository: RequestRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        request = Request()
        restClient = RestClient(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentRequestBinding.inflate(inflater, container, false)

        // Todo: Create a ViewModel for RequestFragment and move this there.
        requestRepository = RequestRepository(requireActivity().application)

        initializeViews()
        return binding.root
    }

    /**
     * Initialize all the views on Request screen.
     */
    private fun initializeViews() {
        binding.etInputUrl.setText(R.string.url_default_text)
        binding.etInputUrl.setSelection(getString(R.string.url_default_text).length)
        binding.requestTypesSpinner.setSelection(0)
        binding.etRequestBody.text.clear()
        headersRecyclerViewAdapter = HeadersRecyclerViewAdapter(false,
                request.headers, this)
        bindViews()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val requestId = requireArguments().getLong(KEY_REQUEST_ID)
            if (requestId > 0) {
                fetchRequestById(requestId)
            }
        }
    }

    /**
     * Reset the values of all the views on Request screen to empty or their default values.
     */
    private fun resetRequestViews() {
        request = Request()
        stopObservingRequestById()
        requireArguments().clear()
        initializeViews()
    }

    /**
     * Stops observing the current request for new updates.
     */
    private fun stopObservingRequestById() {
        requestWithHeadersLiveData.removeObservers(viewLifecycleOwner)
    }

    /**
     * Fetch a request by it's ID.
     *
     * @param requestId The ID of the request to be fetched.
     */
    private fun fetchRequestById(requestId: Long) {
        requestWithHeadersLiveData = requestRepository.getRequestById(requestId)
        requestWithHeadersLiveData
                .observe(viewLifecycleOwner, { requestWithHeaders: RequestWithHeaders? ->
                    // Update the existing headers list object itself and set it in the Request object
                    // since that's the one "HeadersRecyclerViewAdapter" is using to populate the list.
                    if (requestWithHeaders != null) {
                        val existingHeadersList = request.headers
                        existingHeadersList.clear()
                        existingHeadersList.addAll(requestWithHeaders.headers!!)

                        // Create a deep copy of the request object so we don't update the original
                        // request object.
                        request = requestWithHeaders.request.copyRequest()
                        request.headers = existingHeadersList
                        updateViewsWithRequestData(request)
                    }
                })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.request_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_send_request -> {
                MiscUtil.hideKeyboard(context, requireActivity())
                sendRequest()
                true
            }
            R.id.action_save_request -> {
                saveRequest()
                true
            }
            R.id.action_clear_request -> {
                resetRequestViews()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        mListener = try {
            context as OnResponseReceivedListener
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString()
                    + " must implement OnResponseReceivedListener")
        }
    }

    /**
     * Bind the views to their respective necessary attributes.
     */
    private fun bindViews() {
        val linearLayoutManager = LinearLayoutManager(context)
        binding.headersRecyclerView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(context,
                linearLayoutManager.orientation)
        binding.headersRecyclerView.addItemDecoration(dividerItemDecoration)
        binding.headersRecyclerView.adapter = headersRecyclerViewAdapter
        binding.etRequestBody.setOnTouchListener(getTouchListenerForScrollableViews())
        binding.addHeaderFab.setOnClickListener { displayAddHeaderDialog() }
        binding.requestTypesSpinner.onItemSelectedListener = getRequestTypesSpinnerListener()
    }

    /**
     * Updates all the views on Request screen with the Request data passed in.
     *
     * @param request The [Request] whose data is to be filled into the screen.
     */
    private fun updateViewsWithRequestData(request: Request) {
        // Set Request type.
        val requestTypeList = listOf(*resources.getStringArray(R.array.requestTypes))
        binding.requestTypesSpinner.setSelection(requestTypeList.indexOf(request.requestType.name))

        // Set Request URL.
        binding.etInputUrl.setText(request.url)

        // Set Request body text.
        if (request.body != null && request.body!!.isNotEmpty()) {
            binding.etRequestBody.setText(HttpUtil.getFormattedJsonText(request.body))
        }

        // Refresh the headers list.
        headersRecyclerViewAdapter.notifyDataSetChanged()
    }// Disallow the touch request for parent scroll on touch of child view.// Setting on Touch Listener for handling the touch inside ScrollView

    /**
     * Touch listener for the scrollable views to be able scroll independently inside the parent scroll view.
     */
    private fun getTouchListenerForScrollableViews(): View.OnTouchListener {
        // Setting on Touch Listener for handling the touch inside ScrollView
        return View.OnTouchListener { v: View, event: MotionEvent? ->
            // Disallow the touch request for parent scroll on touch of child view.
            v.parent.requestDisallowInterceptTouchEvent(true)
            false
        }
    }

    /**
     * Make the appropriate type of rest request call based on the request type chosen by user.
     */
    private fun sendRequest() {
        // Verify that the input url is a non-empty valid url and
        // internet connectivity is available before proceeding.
        val url = binding.etInputUrl.text.toString()
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            MiscUtil.displayLongToast(context, R.string.invalid_url_msg)
            return
        } else if (!HttpUtil.isNetworkAvailable(context)) {
            MiscUtil.displayShortToast(context, R.string.no_connection_msg)
            return
        }

        val request = prepareRequestObject()
        try {
            MiscUtil.showSpinner(activity)
            when (request.requestType) {
                RequestType.GET -> restClient[request.url, request.headers, requestCallback]
                RequestType.POST -> restClient.post(request.url, request.headers, request.body, requestCallback)
                RequestType.PUT -> restClient.put(request.url, request.headers, request.body, requestCallback)
                RequestType.DELETE -> restClient.delete(request.url, request.headers, request.body, requestCallback)
                RequestType.HEAD -> restClient.head(request.url, request.headers, requestCallback)
                RequestType.PATCH -> restClient.patch(request.url, request.headers, request.body, requestCallback)
            }
        } catch (e: IllegalArgumentException) {
            // If any headers have an invalid value, then IllegalArgumentException is thrown.
            MiscUtil.hideSpinner(activity)
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            return
        }

        // Before saving the request in History, set the necessary values and clear the IDs of the
        // Request and all it's headers (to prevent duplicate insertion) in case user is seeing
        // some pre-saved or historic request.
        request.isInHistory = true
        request.isSaved = false
        request.clearIds()
        requestRepository.insertRequest(request)
    }

    /**
     * Prepares the Request object before sending the request.
     *
     * @return The prepared instance of [Request]
     */
    private fun prepareRequestObject(): Request {
        request.url = binding.etInputUrl.text.toString()

        val requestTypeString = binding.requestTypesSpinner.selectedItem as String
        val requestType = getRequestType(requestTypeString)
        request.requestType = requestType!!

        if (requestType !== RequestType.GET && requestType !== RequestType.HEAD) {
            request.body = binding.etRequestBody.text.toString()
        } else {
            // Explicitly remove any existing body contents in case user changed the request type
            // from a type which accepts a request body (eg POST) to one which doesn't (eg GET).
            request.body = null
        }

        request.updatedAt = DateFormatHelper.getCurrentDate()
        return request
    }

    /**
     * Creates and returns the callback for handling the response received after sending the request.
     *
     * @return The instance of [Callback] created.
     */
    private val requestCallback: Callback
        get() = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (!isVisible) {
                    return
                }

                MiscUtil.hideSpinner(activity)
                if (call.isCanceled()) {
                    return
                }

                handleError(e)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (!isVisible) {
                    return
                }

                val responseBody = response.body
                if (responseBody != null) {
                    val url = response.request.url.toString()
                    val requestTime = response.receivedResponseAtMillis - response.sentRequestAtMillis
                    val responseHeaders = headersToCharSequence(response.headers)
                    val restResponse = RestResponse(response.code, requestTime, url, responseHeaders, responseBody.string())
                    requireActivity().runOnUiThread { mListener.onResponseReceived(restResponse) }
                }

                MiscUtil.hideSpinner(activity)
            }

            /**
             * Convert the list of response headers received into CharSequence format to be displayed to user.
             * @param headers: List of headers received in response.
             * @return List of headers received in CharSequence format.
             */
            private fun headersToCharSequence(headers: Headers): CharSequence {
                val spannableStringBuilder = SpannableStringBuilder()
                var i = 0
                val size = headers.size
                while (i < size) {
                    val value: Spannable = SpannableString(headers.value(i))
                    value.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(),
                            R.color.tertiary_text_light)), 0, value.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableStringBuilder.append(headers.name(i)).append(": ").append(value).append("\n")
                    i++
                }
                spannableStringBuilder.delete(spannableStringBuilder.length - 1, spannableStringBuilder
                        .length)
                return spannableStringBuilder
            }
        }

    /**
     * Handle the error on UI thread.
     *
     * @param exception The exception that caused the rest call to fail.
     */
    private fun handleError(exception: IOException) {
        requireActivity().runOnUiThread { MiscUtil.displayLongToast(context, exception.message) }
    }

    /**
     * Get the corresponding RequestTypeEnum instance based on the String parameter received.
     *
     * @return RequestType enum instance.
     */
    private fun getRequestType(requestTypeString: String): RequestType? {
        for (type in RequestType.values()) {
            if (TextUtils.equals(requestTypeString, type.toString())) {
                return type
            }
        }

        // We shouldn't reach here.
        Log.e(TAG, " : Unidentified Request type : $requestTypeString")
        return null
    }

    /**
     * Creates and returns the listener to handle the event when user selects a request type
     * from the spinner.
     *
     * @return The created instance of [AdapterView.OnItemSelectedListener]
     */
    private fun getRequestTypesSpinnerListener(): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                /**
                 * Show/hide request body area based on the request type chosen by user.
                 */
                val selectedRequestTypeString = parent?.getItemAtPosition(position) as String
                if (TextUtils.equals(selectedRequestTypeString, RequestType.GET.toString())
                        || TextUtils.equals(selectedRequestTypeString, RequestType.HEAD.toString())) {
                    binding.layoutRequestBody.visibility = View.GONE
                } else {
                    binding.layoutRequestBody.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing.
            }
        }
    }

    /**
     * Saves the Request in the local DB after performing validation of all the values entered.
     */
    private fun saveRequest() {
        val url = binding.etInputUrl.text.toString()
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            MiscUtil.displayLongToast(context, R.string.invalid_url_msg)
            return
        }

        // If user came here via saved requests, then ask if they wanna update it or save a new request.
        if (request.requestId > 0 && request.isSaved) {
            showUpdateRequestDialog()
        } else {
            insertOrUpdateRequestInDb(true, R.string.request_saved)
        }
    }

    /**
     * Shows the Update Request dialog.
     */
    private fun showUpdateRequestDialog() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.update_request_dialog_title)
                .setMessage(R.string.update_request_dialog_message)
                .setPositiveButton(R.string.update_request_dialog_update) { dialogInterface: DialogInterface, i: Int ->
                    insertOrUpdateRequestInDb(false, R.string.request_updated)
                    dialogInterface.dismiss()
                }
                .setNeutralButton(R.string.update_request_dialog_create_new) { dialogInterface: DialogInterface, i: Int ->
                    insertOrUpdateRequestInDb(true, R.string.request_saved)
                    dialogInterface.dismiss()
                }
                .show()
    }

    /**
     * Inserts a new one or updates an existing request in the local DB based on the value of shouldInsert param.
     *
     * @param shouldInsert     Boolean indicating if the request should be inserted as a new one or
     * should update an existing one in the DB.
     * @param messageToDisplay The Toast message to display to the user based on whether the request
     * is updated or inserted as a new one.
     */
    private fun insertOrUpdateRequestInDb(shouldInsert: Boolean, messageToDisplay: Int) {
        val request = prepareRequestObject()
        request.isInHistory = false
        request.isSaved = true

        if (shouldInsert) {
            request.clearIds()
            requestRepository.insertRequest(request)
        } else {
            val existingRequestWithHeaders = requestWithHeadersLiveData.value
            requestRepository.update(request, existingRequestWithHeaders!!.headers!!)
        }
        MiscUtil.displayShortToast(context, messageToDisplay)
    }

    /**
     * Display the add header dialog to allow the user to add a new header.
     */
    private fun displayAddHeaderDialog() {
        // Since the user wants to add a new header, pass the NEW_HEADER_POSITION as position for this header.
        displayEditHeaderDialog(NEW_HEADER_POSITION)
    }

    /**
     * Display the edit header dialog with pre-filled info to allow a user to edit an existing header.
     *
     * @param position : The current position of this header in the list of headers already added by user.
     */
    private fun displayEditHeaderDialog(position: Int) {
        var header: Header? = null
        // If the position is not equals to the default position for a new header i.e. NEW_HEADER_POSITION,
        // that means a header already exists at that position.
        val isExistingHeader = position != NEW_HEADER_POSITION

        // Get the existing header object from the list if the dialog is being opened to edit an existing header.
        if (isExistingHeader) {
            header = request.headers[position]
        }

        val addHeaderBinding = DialogAddHeaderBinding.inflate(layoutInflater)
        val alertDialogBuilder = AlertDialog.Builder(
                requireContext())
        alertDialogBuilder.setView(addHeaderBinding.root)

        // create alert dialog
        val alertDialog = alertDialogBuilder.create()

        // Bind views.
        val headerTypesSpinnerAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.headerTypes, android.R.layout.simple_spinner_item)
        headerTypesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        addHeaderBinding.spinnerHeaderTypes.adapter = headerTypesSpinnerAdapter
        addHeaderBinding.spinnerHeaderTypes.onItemSelectedListener = getHeaderTypesSpinnerListener(addHeaderBinding.layoutHeaderFields2,
                addHeaderBinding.tvHeaderLabel1, addHeaderBinding.tvHeaderLabel2)
        addHeaderBinding.okButton.setOnClickListener(getOkButtonClickListener(addHeaderBinding.spinnerHeaderTypes,
                addHeaderBinding.etHeaderValue1, addHeaderBinding.etHeaderValue2, alertDialog, position))
        addHeaderBinding.cancelButton.setOnClickListener {
            MiscUtil.hideKeyboard(context, activity)
            if (alertDialog.isShowing) {
                alertDialog.dismiss()
            }
        }

        // If the header dialog was opened to edit an existing header, pre-fill the header fields with existing info.
        if (isExistingHeader && header != null) {
            when (header.getHeaderTypeEnum()) {
                HeaderType.AUTHORIZATION_BASIC -> {
                    addHeaderBinding.spinnerHeaderTypes.setSelection(headerTypesSpinnerAdapter.getPosition(HeaderType.AUTHORIZATION_BASIC.toString()))
                    val decodedCreds = HttpUtil.getBase64DecodedAuthCreds(header.headerValue)
                    val creds = decodedCreds.split(":".toRegex()).toTypedArray()
                    addHeaderBinding.etHeaderValue1.setText(creds[0])
                    addHeaderBinding.etHeaderValue2.setText(creds[1])
                }
                HeaderType.CUSTOM -> {
                    addHeaderBinding.spinnerHeaderTypes.setSelection(headerTypesSpinnerAdapter.getPosition(HeaderType.CUSTOM.toString()))
                    addHeaderBinding.etHeaderValue1.setText(header.headerType)
                    addHeaderBinding.etHeaderValue2.setText(header.headerValue)
                }
                else -> {
                    addHeaderBinding.spinnerHeaderTypes.setSelection(headerTypesSpinnerAdapter.getPosition(header.headerType))
                    addHeaderBinding.etHeaderValue1.setText(header.headerValue)
                }
            }
        }

        alertDialog.show()
    }

    /**
     * Creates and gets a click listener for when user taps the Ok button to add/update a header.
     *
     * @param headerTypesSpinner The instance of the Header type spinner.
     * @param etUserInput1       Reference to the Input field 1.
     * @param etUserInput2       Reference to the Input field 2.
     * @param alertDialog        Reference to the Add/update header dialog being shown to the user.
     * @param position           The position type for the current header.
     * @return The instance of the [View.OnClickListener] created.
     */
    private fun getOkButtonClickListener(headerTypesSpinner: Spinner, etUserInput1: EditText,
                                         etUserInput2: EditText, alertDialog: AlertDialog?,
                                         position: Int): View.OnClickListener {
        return object : View.OnClickListener {
            override fun onClick(v: View) {
                val isExistingHeader = position != NEW_HEADER_POSITION
                val headerType = getHeaderType(headerTypesSpinner.selectedItem as String)

                val userInput1 = etUserInput1.text.toString().trim()
                val userInput2 = etUserInput2.text.toString().trim()
                val isAuthBasicHeader = headerType === HeaderType.AUTHORIZATION_BASIC
                val isCustomHeader = headerType === HeaderType.CUSTOM

                // Perform add/update operations on the current header based on header type.
                if (isAuthBasicHeader || isCustomHeader) {
                    if (TextUtils.isEmpty(userInput1) || TextUtils.isEmpty(userInput2)) {
                        MiscUtil.displayShortToast(context, R.string.input_fields_empty_msg)
                        return
                    }
                    if (isCustomHeader && MiscUtil.containsWhiteSpaces(userInput1)) {
                        MiscUtil.displayLongToast(context, R.string.custom_header_name_no_whitespaces)
                        return
                    }

                    // Update the header if editing an existing header, add a new one otherwise.
                    if (isExistingHeader) {
                        updateHeader(headerType, etUserInput1.text.toString().trim(),
                                etUserInput2.text.toString().trim(), position)
                    } else {
                        addHeader(headerType, etUserInput1.text.toString().trim(),
                                etUserInput2.text.toString().trim())
                    }
                } else {
                    if (TextUtils.isEmpty(userInput1)) {
                        MiscUtil.displayShortToast(context, R.string.input_fields_empty_msg)
                        return
                    }

                    // Update the header if editing an existing header, add a new one otherwise.
                    if (isExistingHeader) {
                        updateHeader(headerType, etUserInput1.text.toString().trim(), position)
                    } else {
                        addHeader(headerType, etUserInput1.text.toString().trim())
                    }
                }

                // Hide the keyboard and dismiss dialog since header has been added/updated at this point.
                MiscUtil.hideKeyboard(context, activity)
                if (alertDialog != null && alertDialog.isShowing) {
                    alertDialog.dismiss()
                }
            }

            /**
             * Get the corresponding HeaderTypeEnum instance based on the String parameter received.
             * @return HeaderType enum instance.
             */
            private fun getHeaderType(headerTypeString: String): HeaderType {
                for (type in HeaderType.values()) {
                    if (TextUtils.equals(headerTypeString, type.toString())) {
                        return type
                    }
                }
                return HeaderType.CUSTOM
            }
        }
    }

    /**
     * Gets the listener to handle the event when user selects the header type from the list.
     *
     * @param layoutHeaderFields2 The view for layout type 2 which is shown if user selects Custom
     * or Basic Authorization header types.
     * @param tvHeaderLabel1      The view of the label for input field 1.
     * @param tvHeaderLabel2      The view of the label for input field 2.
     * @return An instance of the [AdapterView.OnItemSelectedListener] created.
     */
    private fun getHeaderTypesSpinnerListener(layoutHeaderFields2: LinearLayout, tvHeaderLabel1: TextView,
                                              tvHeaderLabel2: TextView): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                /*
                  Show/hide certain views based on the header type chosen by user.
                 */
                when (parent.getItemAtPosition(position) as String) {
                    getString(R.string.auth_basic) -> {
                        // Show Username and Password fields if header type chosen is "Authorization (Basic)".
                        layoutHeaderFields2.visibility = View.VISIBLE
                        tvHeaderLabel1.setText(R.string.username)
                        tvHeaderLabel2.setText(R.string.password)
                    }
                    getString(R.string.custom) -> {
                        // Show HeaderName and HeaderValue fields if header type chosen is "Custom".
                        layoutHeaderFields2.visibility = View.VISIBLE
                        tvHeaderLabel1.setText(R.string.header_name)
                        tvHeaderLabel2.setText(R.string.header_value)
                    }
                    else -> {
                        // Show only the Value field if header type chosen is any other apart from the above two.
                        layoutHeaderFields2.visibility = View.GONE
                        tvHeaderLabel1.setText(R.string.value)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    /**
     * Update the header in the list of headers with the new info provided.
     *
     * @param headerType The new [Header.HeaderType]
     * @param userInput1 The value from the first input field.
     * @param position:  The position of existing header.
     */
    private fun updateHeader(headerType: HeaderType, userInput1: String, position: Int) {
        val existingHeader = request.headers[position]
        val updatedHeader = getNewHeader(headerType, userInput1)
        updatedHeader.headerId = existingHeader.headerId

        // Remove the existing header from the list and add the updated one at the same position.
        request.headers.removeAt(position)
        request.headers.add(position, updatedHeader)
        headersRecyclerViewAdapter.notifyDataSetChanged()
    }

    /**
     * Update the header in the list of headers with the new info provided.
     *
     * @param headerType The new [HeaderType]
     * @param userInput1 The value from the first input field.
     * @param userInput2 The value from the second input field.
     * @param position:  The position of existing header.
     */
    private fun updateHeader(headerType: HeaderType, userInput1: String, userInput2: String, position: Int) {
        val existingHeader = request.headers[position]
        val updatedHeader = getNewHeader(headerType, userInput1, userInput2)
        updatedHeader.headerId = existingHeader.headerId

        // Remove the existing header from the list and add the updated one at the same position.
        request.headers.removeAt(position)
        request.headers.add(position, updatedHeader)
        headersRecyclerViewAdapter.notifyDataSetChanged()
    }

    /**
     * Create a new header with the info provided by user and add it to the list of headers.
     *
     * @param headerType The [Header.HeaderType] of the header to be added.
     * @param userInput1 The value from the first input field that user entered.
     */
    private fun addHeader(headerType: HeaderType, userInput1: String) {
        val header = getNewHeader(headerType, userInput1)
        request.headers.add(header)
        headersRecyclerViewAdapter.notifyDataSetChanged()
    }

    /**
     * Create a new header with the info provided by use and add it to the list of headers.
     *
     * @param headerType The [Header.HeaderType] of the header to be added.
     * @param userInput1 The value from the first input field that user entered.
     * @param userInput2 The value from the second input field that user entered.
     */
    private fun addHeader(headerType: HeaderType, userInput1: String, userInput2: String) {
        val header = getNewHeader(headerType, userInput1, userInput2)
        request.headers.add(header)
        headersRecyclerViewAdapter.notifyDataSetChanged()
    }

    /**
     * Create a new header object based on user provided info.
     *
     * @param headerType The [Header.HeaderType] of the new header to be created.
     * @param userInput1 The value from the first input field that user entered.
     * @return The newly created Header object.
     */
    private fun getNewHeader(headerType: HeaderType, userInput1: String): Header {
        return Header(headerType.toString(), userInput1)
    }

    /**
     * Create a new header object based on user provided info.
     *
     * @param headerType The [Header.HeaderType] of the header to be added.
     * @param userInput1 The value from the first input field that user entered.
     * @param userInput2 The value from the second input field that user entered.
     * @return The newly created Header object.
     */
    private fun getNewHeader(headerType: HeaderType, userInput1: String, userInput2: String): Header {
        return if (headerType === HeaderType.AUTHORIZATION_BASIC) {
            val headerValue = HttpUtil.getBase64EncodedAuthCreds(context, userInput1, userInput2)
            Header(HeaderType.AUTHORIZATION_BASIC.toString(), headerValue)
        } else {
            Header(userInput1, userInput2)
        }
    }

    override fun onDeleteHeaderClicked(position: Int) {
        // Delete the header at position received and update the header view list.
        val lastDeletedHeaderObject = request.headers[position]

        request.headers.removeAt(position)
        binding.headersRecyclerView.removeViewAt(position)
        headersRecyclerViewAdapter.notifyItemRemoved(position)
        headersRecyclerViewAdapter.notifyItemRangeChanged(position, request.headers.size)

        // Show a confirmation of header deletion and an option for user to undo header deletion.
        val snackbar = Snackbar
                .make(requireView(), R.string.header_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) { view: View? -> restoreLastDeletedHeader(lastDeletedHeaderObject, position) }
        snackbar.show()
    }

    override fun onEditHeaderClicked(position: Int) {
        displayEditHeaderDialog(position)
    }

    /**
     * Restore the last deleted header to it's original position in the list of headers.
     */
    private fun restoreLastDeletedHeader(lastDeletedHeaderObject: Header, lastDeletedHeaderPosition: Int) {
        request.headers.add(lastDeletedHeaderPosition, lastDeletedHeaderObject)
        headersRecyclerViewAdapter.notifyDataSetChanged()
        val snackbar = Snackbar.make(requireView(), R.string.header_restored, Snackbar.LENGTH_SHORT)
        snackbar.show()
    }

    /**
     * Interface to listen for the event when the response for the corresponding request made is available.
     */
    interface OnResponseReceivedListener {
        /**
         * Handles the event when a response is received after user sends the request.
         *
         * @param restResponse The [RestResponse] received.
         */
        fun onResponseReceived(restResponse: RestResponse)
    }
}