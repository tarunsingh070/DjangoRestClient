/*
 * Copyright (C) 2018 Django Rest Client Project, DjangoTech - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited, proprietary and confidential.
 * Written by Tarun Singh <tarunsingh070@gmail.com>, March 2018.
 */

package tarun.djangorestclient.com.djangorestclient.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;
import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.adapter.HeadersRecyclerViewAdapter;
import tarun.djangorestclient.com.djangorestclient.model.AuthBasicHeader;
import tarun.djangorestclient.com.djangorestclient.model.CustomHeader;
import tarun.djangorestclient.com.djangorestclient.model.Header;
import tarun.djangorestclient.com.djangorestclient.model.Header.HeaderType;
import tarun.djangorestclient.com.djangorestclient.model.Request;
import tarun.djangorestclient.com.djangorestclient.model.Request.RequestType;
import tarun.djangorestclient.com.djangorestclient.model.RestResponse;
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

    private EditText etInputUrl;
    private EditText etRequestBody;
    private LinearLayout layoutRequestBody;
    private FloatingActionButton addHeaderFab;
    private Spinner requestTypesSpinner;
    private RecyclerView headersRecyclerView;
    private HeadersRecyclerViewAdapter headersRecyclerViewAdapter;

    private Request request;

    private OnResponseReceivedListener mListener;
    private RestClient restClient;

    public RequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment RequestFragment.
     */
    public static RequestFragment newInstance() {
        return new RequestFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_request, container, false);

        etInputUrl = rootView.findViewById(R.id.et_input_url);
        etRequestBody = rootView.findViewById(R.id.et_request_body);
        layoutRequestBody = rootView.findViewById(R.id.layout_request_body);
        headersRecyclerView = rootView.findViewById(R.id.rv_headers);
        addHeaderFab = rootView.findViewById(R.id.fab_addHeader);
        requestTypesSpinner = rootView.findViewById(R.id.spinner_request_types);

        headersRecyclerViewAdapter = new HeadersRecyclerViewAdapter(this, request.getHeaders());

        bindViews();

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.request_fragment_menu, menu);super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_send_request:
                    sendRequest();
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
        headersRecyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(headersRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        headersRecyclerView.addItemDecoration(dividerItemDecoration);
        headersRecyclerView.setAdapter(headersRecyclerViewAdapter);

        etRequestBody.setOnTouchListener(getTouchListenerForScrollableViews());

        addHeaderFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAddHeaderDialog();
            }
        });
        requestTypesSpinner.setOnItemSelectedListener(getRequestTypesSpinnerListener());
    }

    /**
     * Touch listener for the scrollable views to be able scroll independently inside the parent scroll view.
     */
    private View.OnTouchListener getTouchListenerForScrollableViews() {
        return new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view.
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        };
    }

    /**
     * Make the appropriate type of rest request call based on the request type chosen by user.
     */
    private void sendRequest() {
        // Verify that the input url is a non-empty valid http or https url and
        // internet connectivity is available before proceeding.
        String url = etInputUrl.getText().toString();
        if (!(URLUtil.isHttpUrl(url) && url.length() > 7) && !(URLUtil.isHttpsUrl(url) && url.length() > 8)) {
            MiscUtil.displayLongToast(getContext(), R.string.invalid_url_msg);
            return;
        } else if (!HttpUtil.isNetworkAvailable(getContext())) {
            MiscUtil.displayShortToast(getContext(), R.string.no_connection_msg);
            return;
        }

        Request request = prepareRequestObject();

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
    }

    private Request prepareRequestObject() {
        request.setUrl(etInputUrl.getText().toString());

        String requestTypeString = (String) requestTypesSpinner.getSelectedItem();
        RequestType requestType = getRequestType(requestTypeString);
        request.setRequestType(requestType);

        if (requestType != RequestType.GET && requestType != RequestType.HEAD) {
            request.setBody(etRequestBody.getText().toString());
        }

        return request;
    }

    private Callback getRequestCallback() {
        MiscUtil.showSpinner(getActivity());

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

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onResponseReceived(restResponse);
                        }
                    });
                }

                MiscUtil.hideSpinner(getActivity());
            }

            /**
             * Convert the list of response headers received into CharSequence format to be displayed to user.
             * @param headers: List of headers received in response.
             * @return: List of headers received in CharSequence format.
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
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MiscUtil.displayLongToast(getContext(), exception.getMessage());
            }
        });
    }

    /**
     * Get the corresponding RequestTypeEnum instance based on the String parameter received.
     *
     * @return: RequestType enum instance.
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
                    layoutRequestBody.setVisibility(View.GONE);
                } else {
                    layoutRequestBody.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing.
            }
        };
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

        // Inflate dialog_add_header.xml
        LayoutInflater li = LayoutInflater.from(getContext());
        View addHeaderDialogView = li.inflate(R.layout.dialog_add_header, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        // set our custom inflated view to alert dialog builder.
        alertDialogBuilder.setView(addHeaderDialogView);

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        // Initialize views.
        Spinner headerTypesSpinner = addHeaderDialogView.findViewById(R.id.spinner_header_types);
        EditText etUserInput1 = addHeaderDialogView.findViewById(R.id.et_header_value_1);
        EditText etUserInput2 = addHeaderDialogView.findViewById(R.id.et_header_value_2);
        TextView tvHeaderLabel1 = addHeaderDialogView.findViewById(R.id.tv_header_label_1);
        TextView tvHeaderLabel2 = addHeaderDialogView.findViewById(R.id.tv_header_label_2);
        LinearLayout layoutHeaderFields2 = addHeaderDialogView.findViewById(R.id.layout_header_fields_2);
        Button okButton = addHeaderDialogView.findViewById(R.id.button_ok);
        Button cancelButton = addHeaderDialogView.findViewById(R.id.button_cancel);

        // Bind views.
        ArrayAdapter<CharSequence> headerTypesSpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.headerTypes, android.R.layout.simple_spinner_item);
        headerTypesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        headerTypesSpinner.setAdapter(headerTypesSpinnerAdapter);
        headerTypesSpinner.setOnItemSelectedListener(getHeaderTypesSpinnerListener(layoutHeaderFields2, tvHeaderLabel1, tvHeaderLabel2));

        okButton.setOnClickListener(getOkButtonClickListener(headerTypesSpinner, etUserInput1, etUserInput2, alertDialog, position));
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MiscUtil.hideKeyboard(getContext(), getActivity());
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            }
        });

        // If the header dialog was opened to edit an existing header, pre-fill the header fields with existing info.
        if (isExistingHeader && header != null) {
            if (header.getHeaderType() == HeaderType.AUTHORIZATION_BASIC) {
                AuthBasicHeader authBasicHeader = (AuthBasicHeader) header;
                headerTypesSpinner.setSelection(headerTypesSpinnerAdapter.getPosition(HeaderType.AUTHORIZATION_BASIC.toString()));
                etUserInput1.setText(authBasicHeader.getUserName());
                etUserInput2.setText(authBasicHeader.getPassword());
            } else if (header.getHeaderType() == HeaderType.CUSTOM) {
                CustomHeader customHeader = (CustomHeader) header;
                headerTypesSpinner.setSelection(headerTypesSpinnerAdapter.getPosition(HeaderType.CUSTOM.toString()));
                etUserInput1.setText(customHeader.getCustomHeaderType());
                etUserInput2.setText(customHeader.getHeaderValue());
            } else {
                headerTypesSpinner.setSelection(headerTypesSpinnerAdapter.getPosition(header.getHeaderType().toString()));
                etUserInput1.setText(header.getHeaderValue());
            }
        }

        alertDialog.show();
    }

    private View.OnClickListener getOkButtonClickListener(final Spinner headerTypesSpinner, final EditText etUserInput1
            , final EditText etUserInput2, final AlertDialog alertDialog, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean isExistingHeader = (position != NEW_HEADER_POSITION);
                HeaderType headerType = getHeaderType((String) headerTypesSpinner.getSelectedItem());

                String userInput1 = etUserInput1.getText().toString();
                String userInput2 = etUserInput2.getText().toString();

                boolean isAuthBasicHeader = (headerType == HeaderType.AUTHORIZATION_BASIC);
                boolean isCustomHeader = (headerType == HeaderType.CUSTOM);

                // Perform add/update operations on the current header based on header type.
                if (isAuthBasicHeader || isCustomHeader) {
                    if (TextUtils.isEmpty(userInput1) || TextUtils.isEmpty(userInput2)) {
                        MiscUtil.displayShortToast(getContext(), R.string.input_fields_empty_msg);
                        return;
                    }

                    // Update the header if editing an existing header, add a new one otherwise.
                    if (isExistingHeader) {
                        updateHeader(headerType, etUserInput1.getText().toString(), etUserInput2.getText().toString(), position);
                    } else {
                        addHeader(headerType, etUserInput1.getText().toString(), etUserInput2.getText().toString());
                    }

                } else {
                    if (TextUtils.isEmpty(userInput1)) {
                        MiscUtil.displayShortToast(getContext(), R.string.input_fields_empty_msg);
                        return;
                    }

                    // Update the header if editing an existing header, add a new one otherwise.
                    if (isExistingHeader) {
                        updateHeader(headerType, etUserInput1.getText().toString(), position);
                    } else {
                        addHeader(headerType, etUserInput1.getText().toString());
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
             * @return: HeaderType enum instance.
             */
            private HeaderType getHeaderType(String headerTypeString) {
                for (HeaderType type : HeaderType.values()) {
                    if (TextUtils.equals(headerTypeString, type.toString())) {
                        return type;
                    }
                }

                // We shouldn't reach here.
                Log.e(TAG, " : Unidentified Header type : " + headerTypeString);
                return null;
            }
        };
    }

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
     * @param position: The position of existing header.
     */
    private void updateHeader(HeaderType headerType, String userInput1, int position) {
        // Remove the existing header from the list and add the new one at the same position.
        request.getHeaders().remove(position);
        request.getHeaders().add(position, getNewHeader(headerType, userInput1));
        headersRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Update the header in the list of headers with the new info provided.
     *
     * @param position: The position of existing header.
     */
    private void updateHeader(HeaderType headerType, String userInput1, String userInput2, int position) {
        // Remove the existing header from the list and add the new one at the same position.
        request.getHeaders().remove(position);
        request.getHeaders().add(position, getNewHeader(headerType, userInput1, userInput2));
        headersRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Create a new header with the info provided by use and add it to the list of headers.
     */
    private void addHeader(HeaderType headerType, String userInput1) {
        Header header = getNewHeader(headerType, userInput1);
        request.getHeaders().add(header);
        headersRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Create a new header with the info provided by use and add it to the list of headers.
     */
    private void addHeader(HeaderType headerType, String userInput1, String userInput2) {
        Header header = getNewHeader(headerType, userInput1, userInput2);
        request.getHeaders().add(header);
        headersRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Create a new header object based on user provided info.
     *
     * @return: The newly created Header object.
     */
    private Header getNewHeader(HeaderType headerType, String userInput1) {
        return new Header(headerType, userInput1);
    }

    /**
     * Create a new header object based on user provided info.
     *
     * @return: The newly created Header object.
     */
    private Header getNewHeader(HeaderType headerType, String userInput1, String userInput2) {
        if (headerType == HeaderType.AUTHORIZATION_BASIC) {
            String headerValue = HttpUtil.getBase64EncodedAuthCreds(getContext(), userInput1, userInput2);
            return new AuthBasicHeader(headerValue, userInput1, userInput2);
        } else {
            return new CustomHeader(userInput1, userInput2);
        }
    }

    @Override
    public void onDeleteHeaderClicked(final int position) {
        // Delete the header at position received and update the header view list.
        final Header lastDeletedHeaderObject = request.getHeaders().get(position);

        request.getHeaders().remove(position);
        headersRecyclerView.removeViewAt(position);
        headersRecyclerViewAdapter.notifyItemRemoved(position);
        headersRecyclerViewAdapter.notifyItemRangeChanged(position, request.getHeaders().size());

        // Show a confirmation of header deletion and an option for user to undo header deletion.
        Snackbar snackbar = Snackbar
                .make(getView(), R.string.header_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        restoreLastDeletedHeader(lastDeletedHeaderObject, position);
                    }
                });

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
        void onResponseReceived(RestResponse restResponse);
    }

}
