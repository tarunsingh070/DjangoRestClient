/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.fragment;


import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;
import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.adapter.HeadersRecyclerViewAdapter;
import tarun.djangorestclient.com.djangorestclient.databinding.DialogAddHeaderBinding;
import tarun.djangorestclient.com.djangorestclient.databinding.FragmentRequestBinding;
import tarun.djangorestclient.com.djangorestclient.model.RequestRepository;
import tarun.djangorestclient.com.djangorestclient.model.RestResponse;
import tarun.djangorestclient.com.djangorestclient.model.entity.Header;
import tarun.djangorestclient.com.djangorestclient.model.entity.Request;
import tarun.djangorestclient.com.djangorestclient.model.entity.Request.RequestType;
import tarun.djangorestclient.com.djangorestclient.model.entity.RequestWithHeaders;
import tarun.djangorestclient.com.djangorestclient.utils.DateFormatHelper;
import tarun.djangorestclient.com.djangorestclient.utils.HttpUtil;
import tarun.djangorestclient.com.djangorestclient.utils.MiscUtil;
import tarun.djangorestclient.com.djangorestclient.utils.RestClient;

/**
 * This fragment shows user all necessary fields to make REST requests.
 */
public class RequestFragment extends Fragment implements HeadersRecyclerViewAdapter.HeaderOptionsClickedListener {

    public static final String TITLE = "Request";

    private static final String TAG = RequestFragment.class.getSimpleName();

    private static final int NEW_HEADER_POSITION = -1;

    public static final String KEY_REQUEST_ID = "key_request_id";

    private HeadersRecyclerViewAdapter headersRecyclerViewAdapter;

    private FragmentRequestBinding binding;

    private Request request;

    private LiveData<RequestWithHeaders> requestWithHeadersLiveData;

    private OnResponseReceivedListener mListener;
    private RestClient restClient;
    private RequestRepository requestRepository;

    public RequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @param args The arguments to be passed into this fragment.
     * @return A new instance of fragment RequestFragment.
     */
    public static RequestFragment newInstance(Bundle args) {
        RequestFragment fragment = new RequestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        request = new Request();
        restClient = new RestClient(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRequestBinding.inflate(inflater, container, false);

        // Todo: Create a ViewModel for RequestFragment and move this there.
        requestRepository = new RequestRepository(requireActivity().getApplication());

        initializeViews();
        return binding.getRoot();
    }

    /**
     * Initialize all the views on Request screen.
     */
    private void initializeViews() {
        binding.etInputUrl.setText(R.string.url_default_text);
        binding.etInputUrl.setSelection(getString(R.string.url_default_text).length());
        binding.requestTypesSpinner.setSelection(0);
        binding.etRequestBody.getText().clear();
        headersRecyclerViewAdapter = new HeadersRecyclerViewAdapter(false,
                request.getHeaders(), this);
        bindViews();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            long requestId = getArguments().getLong(KEY_REQUEST_ID);
            if (requestId > 0) {
                fetchRequestById(requestId);
            }
        }
    }

    /**
     * Reset the values of all the views on Request screen to empty or their default values.
     */
    private void resetRequestViews() {
        request = new Request();
        if (getArguments() != null) {
            stopObservingRequestById();
            getArguments().clear();
        }
        initializeViews();
    }

    /**
     * Stops observing the current request for new updates.
     */
    private void stopObservingRequestById() {
        if (requestWithHeadersLiveData != null) {
            requestWithHeadersLiveData.removeObservers(getViewLifecycleOwner());
        }
    }

    /**
     * Fetch a request by it's ID.
     *
     * @param requestId The ID of the request to be fetched.
     */
    private void fetchRequestById(long requestId) {
        requestWithHeadersLiveData = requestRepository.getRequestById(requestId);
        requestWithHeadersLiveData
                .observe(getViewLifecycleOwner(), requestWithHeaders -> {
                    // Update the existing headers list object itself and set it in the Request object
                    // since that's the one "HeadersRecyclerViewAdapter" is using to populate the list.
                    if (requestWithHeaders != null) {
                        ArrayList<Header> existingHeadersList = request.getHeaders();
                        existingHeadersList.clear();
                        existingHeadersList.addAll(requestWithHeaders.getHeaders());

                        // Create a deep copy of the request object so we don't update the original
                        // request object.
                        request = new Request(requestWithHeaders.getRequest());
                        request.setHeaders(existingHeadersList);
                        updateViewsWithRequestData(request);
                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.request_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_send_request:
                MiscUtil.hideKeyboard(getContext(), requireActivity());
                sendRequest();
                return true;
            case R.id.action_save_request:
                saveRequest();
                return true;
            case R.id.action_clear_request:
                resetRequestViews();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnResponseReceivedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnResponseReceivedListener");
        }
    }

    /**
     * Bind the views to their respective necessary attributes.
     */
    private void bindViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.headersRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                linearLayoutManager.getOrientation());
        binding.headersRecyclerView.addItemDecoration(dividerItemDecoration);
        binding.headersRecyclerView.setAdapter(headersRecyclerViewAdapter);

        binding.etRequestBody.setOnTouchListener(getTouchListenerForScrollableViews());

        binding.addHeaderFab.setOnClickListener(v -> displayAddHeaderDialog());
        binding.requestTypesSpinner.setOnItemSelectedListener(getRequestTypesSpinnerListener());
    }

    /**
     * Updates all the views on Request screen with the Request data passed in.
     *
     * @param request The {@link Request} whose data is to be filled into the screen.
     */
    private void updateViewsWithRequestData(Request request) {
        // Set Request type.
        List<String> requestTypeList = Arrays.asList(getResources().getStringArray(R.array.requestTypes));
        binding.requestTypesSpinner.setSelection(requestTypeList.indexOf(request.getRequestType().name()));

        // Set Request URL.
        binding.etInputUrl.setText(request.getUrl());

        // Set Request body text.
        if (request.getBody() != null && !request.getBody().isEmpty()) {
            binding.etRequestBody.setText(HttpUtil.getFormattedJsonText(request.getBody()));
        }

        // Refresh the headers list.
        headersRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Touch listener for the scrollable views to be able scroll independently inside the parent scroll view.
     */
    private View.OnTouchListener getTouchListenerForScrollableViews() {
        // Setting on Touch Listener for handling the touch inside ScrollView
        return (v, event) -> {
            // Disallow the touch request for parent scroll on touch of child view.
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        };
    }

    /**
     * Make the appropriate type of rest request call based on the request type chosen by user.
     */
    private void sendRequest() {
        // Verify that the input url is a non-empty valid url and
        // internet connectivity is available before proceeding.
        String url = binding.etInputUrl.getText().toString();
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            MiscUtil.displayLongToast(getContext(), R.string.invalid_url_msg);
            return;
        } else if (!HttpUtil.isNetworkAvailable(getContext())) {
            MiscUtil.displayShortToast(getContext(), R.string.no_connection_msg);
            return;
        }

        Request request = prepareRequestObject();
        try {
            MiscUtil.showSpinner(getActivity());

            switch (request.getRequestType()) {
                case GET:
                    restClient.get(request.getUrl(), request.getHeaders(), getRequestCallback());
                    break;

                case POST:
                    restClient.post(request.getUrl(), request.getHeaders(), request.getBody(), getRequestCallback());
                    break;

                case PUT:
                    restClient.put(request.getUrl(), request.getHeaders(), request.getBody(), getRequestCallback());
                    break;

                case DELETE:
                    restClient.delete(request.getUrl(), request.getHeaders(), request.getBody(), getRequestCallback());
                    break;

                case HEAD:
                    restClient.head(request.getUrl(), request.getHeaders(), getRequestCallback());
                    break;

                case PATCH:
                    restClient.patch(request.getUrl(), request.getHeaders(), request.getBody(), getRequestCallback());
                    break;
            }
        } catch (IllegalArgumentException e) {
            // If any headers have an invalid value, then IllegalArgumentException is thrown.
            MiscUtil.hideSpinner(getActivity());
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // Before saving the request in History, set the necessary values and clear the IDs of the
        // Request and all it's headers (to prevent duplicate insertion) in case user is seeing
        // some pre-saved or historic request.
        request.setInHistory(true);
        request.setSaved(false);
        request.clearIds();
        requestRepository.insertRequest(request);
    }

    /**
     * Prepares the Request object before sending the request.
     *
     * @return The prepared instance of {@link Request}
     */
    private Request prepareRequestObject() {
        request.setUrl(binding.etInputUrl.getText().toString());

        String requestTypeString = (String) binding.requestTypesSpinner.getSelectedItem();
        RequestType requestType = getRequestType(requestTypeString);
        request.setRequestType(requestType);

        if (requestType != RequestType.GET && requestType != RequestType.HEAD) {
            request.setBody(binding.etRequestBody.getText().toString());
        }

        request.setUpdatedAt(DateFormatHelper.getCurrentDate());
        return request;
    }

    /**
     * Creates and returns the callback for handling the response received after sending the request.
     *
     * @return The instance of {@link Callback} created.
     */
    private Callback getRequestCallback() {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (!isVisible()) {
                    return;
                }

                MiscUtil.hideSpinner(getActivity());
                if (call.isCanceled()) {
                    return;
                }

                handleError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!isVisible()) {
                    return;
                }

                ResponseBody responseBody = response.body();
                if (responseBody != null) {

                    String url = response.request().url().toString();
                    long requestTime = response.receivedResponseAtMillis() - response.sentRequestAtMillis();
                    CharSequence responseHeaders = headersToCharSequence(response.headers());

                    final RestResponse restResponse = new RestResponse(response.code(), requestTime, url
                            , responseHeaders, responseBody.string());

                    getActivity().runOnUiThread(() -> mListener.onResponseReceived(restResponse));
                }

                MiscUtil.hideSpinner(getActivity());
            }

            /**
             * Convert the list of response headers received into CharSequence format to be displayed to user.
             * @param headers: List of headers received in response.
             * @return List of headers received in CharSequence format.
             */
            private CharSequence headersToCharSequence(Headers headers) {
                if (headers == null) return null;
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                for (int i = 0, size = headers.size(); i < size; i++) {
                    Spannable value = new SpannableString(headers.value(i));
                    value.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.tertiary_text_light))
                            , 0, value.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.append(headers.name(i)).append(": ").append(value).append("\n");
                }
                spannableStringBuilder.delete(spannableStringBuilder.length() - 1, spannableStringBuilder
                        .length());
                return spannableStringBuilder;
            }
        };
    }

    /**
     * Handle the error on UI thread.
     *
     * @param exception The exception that caused the rest call to fail.
     */
    private void handleError(final IOException exception) {
        getActivity().runOnUiThread(() -> MiscUtil.displayLongToast(getContext(), exception.getMessage()));
    }

    /**
     * Get the corresponding RequestTypeEnum instance based on the String parameter received.
     *
     * @return RequestType enum instance.
     */
    private Request.RequestType getRequestType(String requestTypeString) {
        for (RequestType type : RequestType.values()) {
            if (TextUtils.equals(requestTypeString, type.toString())) {
                return type;
            }
        }

        // We shouldn't reach here.
        Log.e(TAG, " : Unidentified Request type : " + requestTypeString);
        return null;
    }

    /**
     * Creates and returns the listener to handle the event when user selects a request type
     * from the spinner.
     *
     * @return The created instance of {@link AdapterView.OnItemSelectedListener}
     */
    private AdapterView.OnItemSelectedListener getRequestTypesSpinnerListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*
                  Show/hide request body area based on the request type chosen by user.
                 */
                String selectedRequestTypeString = (String) parent.getItemAtPosition(position);
                if (TextUtils.equals(selectedRequestTypeString, RequestType.GET.toString())
                        || TextUtils.equals(selectedRequestTypeString, RequestType.HEAD.toString())) {
                    binding.layoutRequestBody.setVisibility(View.GONE);
                } else {
                    binding.layoutRequestBody.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        };
    }

    /**
     * Saves the Request in the local DB after performing validation of all the values entered.
     */
    private void saveRequest() {
        String url = binding.etInputUrl.getText().toString();
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            MiscUtil.displayLongToast(getContext(), R.string.invalid_url_msg);
            return;
        }

        // If user came here via saved requests, then ask if they wanna update it or save a new request.
        if (request.getRequestId() > 0 && request.isSaved()) {
            showUpdateRequestDialog();
        } else {
            insertOrUpdateRequestInDb(true, R.string.request_saved);
        }
    }

    /**
     * Shows the Update Request dialog.
     */
    void showUpdateRequestDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.update_request_dialog_title)
                .setMessage(R.string.update_request_dialog_message)
                .setPositiveButton(R.string.update_request_dialog_update, (dialogInterface, i) -> {
                    insertOrUpdateRequestInDb(false, R.string.request_updated);
                    dialogInterface.dismiss();
                })
                .setNeutralButton(R.string.update_request_dialog_create_new, (dialogInterface, i) -> {
                    insertOrUpdateRequestInDb(true, R.string.request_saved);
                    dialogInterface.dismiss();
                })
                .show();
    }

    /**
     * Inserts a new one or updates an existing request in the local DB based on the value of shouldInsert param.
     *
     * @param shouldInsert     Boolean indicating if the request should be inserted as a new one or
     *                         should update an existing one in the DB.
     * @param messageToDisplay The Toast message to display to the user based on whether the request
     *                         is updated or inserted as a new one.
     */
    private void insertOrUpdateRequestInDb(boolean shouldInsert, int messageToDisplay) {
        Request request = prepareRequestObject();
        request.setInHistory(false);
        request.setSaved(true);

        if (shouldInsert) {
            request.clearIds();
            requestRepository.insertRequest(request);
        } else {
            RequestWithHeaders existingRequestWithHeaders = requestWithHeadersLiveData.getValue();
            requestRepository.update(request, existingRequestWithHeaders.getHeaders());
        }
        MiscUtil.displayShortToast(getContext(), messageToDisplay);
    }

    /**
     * Display the add header dialog to allow the user to add a new header.
     */
    private void displayAddHeaderDialog() {
        // Since the user wants to add a new header, pass the NEW_HEADER_POSITION as position for this header.
        displayEditHeaderDialog(NEW_HEADER_POSITION);
    }

    /**
     * Display the edit header dialog with pre-filled info to allow a user to edit an existing header.
     *
     * @param position : The current position of this header in the list of headers already added by user.
     */
    private void displayEditHeaderDialog(int position) {
        Header header = null;
        // If the position is not equals to the default position for a new header i.e. NEW_HEADER_POSITION,
        // that means a header already exists at that position.
        final boolean isExistingHeader = (position != NEW_HEADER_POSITION);

        // Get the existing header object from the list if the dialog is being opened to edit an existing header.
        if (isExistingHeader) {
            header = request.getHeaders().get(position);
        }

        DialogAddHeaderBinding addHeaderBinding = DialogAddHeaderBinding.inflate(getLayoutInflater());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());
        alertDialogBuilder.setView(addHeaderBinding.getRoot());

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // Bind views.
        ArrayAdapter<CharSequence> headerTypesSpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.headerTypes, android.R.layout.simple_spinner_item);
        headerTypesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addHeaderBinding.spinnerHeaderTypes.setAdapter(headerTypesSpinnerAdapter);
        addHeaderBinding.spinnerHeaderTypes
                .setOnItemSelectedListener(getHeaderTypesSpinnerListener(addHeaderBinding.layoutHeaderFields2,
                        addHeaderBinding.tvHeaderLabel1, addHeaderBinding.tvHeaderLabel2));

        addHeaderBinding.okButton.setOnClickListener(getOkButtonClickListener(addHeaderBinding.spinnerHeaderTypes,
                addHeaderBinding.etHeaderValue1, addHeaderBinding.etHeaderValue2, alertDialog, position));
        addHeaderBinding.cancelButton.setOnClickListener(v -> {
            MiscUtil.hideKeyboard(getContext(), getActivity());
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        });

        // If the header dialog was opened to edit an existing header, pre-fill the header fields with existing info.
        if (isExistingHeader && header != null) {
            if (header.getHeaderTypeEnum() == Header.HeaderType.AUTHORIZATION_BASIC) {
                addHeaderBinding.spinnerHeaderTypes.setSelection(headerTypesSpinnerAdapter.getPosition(Header.HeaderType.AUTHORIZATION_BASIC.toString()));

                String decodedCreds = HttpUtil.getBase64DecodedAuthCreds(header.getHeaderValue());
                String[] creds = decodedCreds.split(":");

                addHeaderBinding.etHeaderValue1.setText(creds[0]);
                addHeaderBinding.etHeaderValue2.setText(creds[1]);
            } else if (header.getHeaderTypeEnum() == Header.HeaderType.CUSTOM) {
                addHeaderBinding.spinnerHeaderTypes.setSelection(headerTypesSpinnerAdapter.getPosition(Header.HeaderType.CUSTOM.toString()));
                addHeaderBinding.etHeaderValue1.setText(header.getHeaderType());
                addHeaderBinding.etHeaderValue2.setText(header.getHeaderValue());
            } else {
                addHeaderBinding.spinnerHeaderTypes.setSelection(headerTypesSpinnerAdapter.getPosition(header.getHeaderType()));
                addHeaderBinding.etHeaderValue1.setText(header.getHeaderValue());
            }
        }

        alertDialog.show();
    }

    /**
     * Creates and gets a click listener for when user taps the Ok button to add/update a header.
     *
     * @param headerTypesSpinner The instance of the Header type spinner.
     * @param etUserInput1       Reference to the Input field 1.
     * @param etUserInput2       Reference to the Input field 2.
     * @param alertDialog        Reference to the Add/update header dialog being shown to the user.
     * @param position           The position type for the current header.
     * @return The instance of the {@link View.OnClickListener} created.
     */
    private View.OnClickListener getOkButtonClickListener(final Spinner headerTypesSpinner, final EditText etUserInput1
            , final EditText etUserInput2, final AlertDialog alertDialog, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isExistingHeader = (position != NEW_HEADER_POSITION);
                Header.HeaderType headerType = getHeaderType((String) headerTypesSpinner.getSelectedItem());

                String userInput1 = etUserInput1.getText().toString().trim();
                String userInput2 = etUserInput2.getText().toString().trim();

                boolean isAuthBasicHeader = (headerType == Header.HeaderType.AUTHORIZATION_BASIC);
                boolean isCustomHeader = (headerType == Header.HeaderType.CUSTOM);

                // Perform add/update operations on the current header based on header type.
                if (isAuthBasicHeader || isCustomHeader) {
                    if (TextUtils.isEmpty(userInput1) || TextUtils.isEmpty(userInput2)) {
                        MiscUtil.displayShortToast(getContext(), R.string.input_fields_empty_msg);
                        return;
                    }

                    if (isCustomHeader && MiscUtil.containsWhiteSpaces(userInput1)) {
                        MiscUtil.displayLongToast(getContext(), R.string.custom_header_name_no_whitespaces);
                        return;
                    }

                    // Update the header if editing an existing header, add a new one otherwise.
                    if (isExistingHeader) {
                        updateHeader(headerType, etUserInput1.getText().toString().trim(),
                                etUserInput2.getText().toString().trim(), position);
                    } else {
                        addHeader(headerType, etUserInput1.getText().toString().trim(),
                                etUserInput2.getText().toString().trim());
                    }

                } else {
                    if (TextUtils.isEmpty(userInput1)) {
                        MiscUtil.displayShortToast(getContext(), R.string.input_fields_empty_msg);
                        return;
                    }

                    // Update the header if editing an existing header, add a new one otherwise.
                    if (isExistingHeader) {
                        updateHeader(headerType, etUserInput1.getText().toString().trim(), position);
                    } else {
                        addHeader(headerType, etUserInput1.getText().toString().trim());
                    }
                }

                // Hide the keyboard and dismiss dialog since header has been added/updated at this point.
                MiscUtil.hideKeyboard(getContext(), getActivity());
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }

            /**
             * Get the corresponding HeaderTypeEnum instance based on the String parameter received.
             * @return HeaderType enum instance.
             */
            private Header.HeaderType getHeaderType(String headerTypeString) {
                for (Header.HeaderType type : Header.HeaderType.values()) {
                    if (TextUtils.equals(headerTypeString, type.toString())) {
                        return type;
                    }
                }

                return Header.HeaderType.CUSTOM;
            }
        };
    }

    /**
     * Gets the listener to handle the event when user selects the header type from the list.
     *
     * @param layoutHeaderFields2 The view for layout type 2 which is shown if user selects Custom
     *                            or Basic Authorization header types.
     * @param tvHeaderLabel1      The view of the label for input field 1.
     * @param tvHeaderLabel2      The view of the label for input field 2.
     * @return An instance of the {@link AdapterView.OnItemSelectedListener} created.
     */
    private AdapterView.OnItemSelectedListener getHeaderTypesSpinnerListener(final LinearLayout layoutHeaderFields2
            , final TextView tvHeaderLabel1, final TextView tvHeaderLabel2) {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*
                  Show/hide certain views based on the header type chosen by user.
                 */
                if (TextUtils.equals(((String) parent.getItemAtPosition(position)), getString(R.string.auth_basic))) {
                    // Show Username and Password fields if header type chosen is "Authorization (Basic)".
                    layoutHeaderFields2.setVisibility(View.VISIBLE);
                    tvHeaderLabel1.setText(R.string.username);
                    tvHeaderLabel2.setText(R.string.password);
                } else if (TextUtils.equals(((String) parent.getItemAtPosition(position)), getString(R.string.custom))) {
                    // Show HeaderName and HeaderValue fields if header type chosen is "Custom".
                    layoutHeaderFields2.setVisibility(View.VISIBLE);
                    tvHeaderLabel1.setText(R.string.header_name);
                    tvHeaderLabel2.setText(R.string.header_value);
                } else {
                    // Show only the Value field if header type chosen is any other apart from the above two.
                    layoutHeaderFields2.setVisibility(View.GONE);
                    tvHeaderLabel1.setText(R.string.value);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        };
    }

    /**
     * Update the header in the list of headers with the new info provided.
     *
     * @param headerType The new {@link Header.HeaderType}
     * @param userInput1 The value from the first input field.
     * @param position:  The position of existing header.
     */
    private void updateHeader(Header.HeaderType headerType, String userInput1, int position) {
        Header existingHeader = request.getHeaders().get(position);
        Header updatedHeader = getNewHeader(headerType, userInput1);
        updatedHeader.setHeaderId(existingHeader.getHeaderId());

        // Remove the existing header from the list and add the updated one at the same position.
        request.getHeaders().remove(position);
        request.getHeaders().add(position, updatedHeader);
        headersRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Update the header in the list of headers with the new info provided.
     *
     * @param headerType The new {@link tarun.djangorestclient.com.djangorestclient.model.entity.Header.HeaderType}
     * @param userInput1 The value from the first input field.
     * @param userInput2 The value from the second input field.
     * @param position:  The position of existing header.
     */
    private void updateHeader(Header.HeaderType headerType, String userInput1, String userInput2, int position) {
        Header existingHeader = request.getHeaders().get(position);
        Header updatedHeader = getNewHeader(headerType, userInput1, userInput2);
        updatedHeader.setHeaderId(existingHeader.getHeaderId());

        // Remove the existing header from the list and add the updated one at the same position.
        request.getHeaders().remove(position);
        request.getHeaders().add(position, updatedHeader);
        headersRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Create a new header with the info provided by user and add it to the list of headers.
     *
     * @param headerType The {@link Header.HeaderType} of the header to be added.
     * @param userInput1 The value from the first input field that user entered.
     */
    private void addHeader(Header.HeaderType headerType, String userInput1) {
        Header header = getNewHeader(headerType, userInput1);
        request.getHeaders().add(header);
        headersRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Create a new header with the info provided by use and add it to the list of headers.
     *
     * @param headerType The {@link Header.HeaderType} of the header to be added.
     * @param userInput1 The value from the first input field that user entered.
     * @param userInput2 The value from the second input field that user entered.
     */
    private void addHeader(Header.HeaderType headerType, String userInput1, String userInput2) {
        Header header = getNewHeader(headerType, userInput1, userInput2);
        request.getHeaders().add(header);
        headersRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Create a new header object based on user provided info.
     *
     * @param headerType The {@link Header.HeaderType} of the new header to be created.
     * @param userInput1 The value from the first input field that user entered.
     * @return The newly created Header object.
     */
    private Header getNewHeader(Header.HeaderType headerType, String userInput1) {
        return new Header(headerType.toString(), userInput1);
    }

    /**
     * Create a new header object based on user provided info.
     *
     * @param headerType The {@link Header.HeaderType} of the header to be added.
     * @param userInput1 The value from the first input field that user entered.
     * @param userInput2 The value from the second input field that user entered.
     * @return The newly created Header object.
     */
    private Header getNewHeader(Header.HeaderType headerType, String userInput1, String userInput2) {
        if (headerType == Header.HeaderType.AUTHORIZATION_BASIC) {
            String headerValue = HttpUtil.getBase64EncodedAuthCreds(getContext(), userInput1, userInput2);
            return new Header(Header.HeaderType.AUTHORIZATION_BASIC.toString(), headerValue);
        } else {
            return new Header(userInput1, userInput2);
        }
    }

    @Override
    public void onDeleteHeaderClicked(final int position) {
        // Delete the header at position received and update the header view list.
        final Header lastDeletedHeaderObject = request.getHeaders().get(position);

        request.getHeaders().remove(position);
        binding.headersRecyclerView.removeViewAt(position);
        headersRecyclerViewAdapter.notifyItemRemoved(position);
        headersRecyclerViewAdapter.notifyItemRangeChanged(position, request.getHeaders().size());

        // Show a confirmation of header deletion and an option for user to undo header deletion.
        Snackbar snackbar = Snackbar
                .make(getView(), R.string.header_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, view -> restoreLastDeletedHeader(lastDeletedHeaderObject, position));

        snackbar.show();
    }

    @Override
    public void onEditHeaderClicked(int position) {
        displayEditHeaderDialog(position);
    }

    /**
     * Restore the last deleted header to it's original position in the list of headers.
     */
    private void restoreLastDeletedHeader(Header lastDeletedHeaderObject, int lastDeletedHeaderPosition) {
        request.getHeaders().add(lastDeletedHeaderPosition, lastDeletedHeaderObject);
        headersRecyclerViewAdapter.notifyDataSetChanged();
        Snackbar snackbar = Snackbar.make(getView(), R.string.header_restored, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    /**
     * Interface to listen for the event when the response for the corresponding request made is available.
     */
    public interface OnResponseReceivedListener {
        /**
         * Handles the event when a response is received after user sends the request.
         *
         * @param restResponse The {@link RestResponse} received.
         */
        void onResponseReceived(RestResponse restResponse);
    }
}
