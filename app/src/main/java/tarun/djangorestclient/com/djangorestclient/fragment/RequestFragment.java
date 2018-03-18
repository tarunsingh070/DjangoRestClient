package tarun.djangorestclient.com.djangorestclient.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.adapter.HeadersRecyclerViewAdapter;
import tarun.djangorestclient.com.djangorestclient.model.AuthBasicHeader;
import tarun.djangorestclient.com.djangorestclient.model.CustomHeader;
import tarun.djangorestclient.com.djangorestclient.model.Header;
import tarun.djangorestclient.com.djangorestclient.model.Header.HeaderType;
import tarun.djangorestclient.com.djangorestclient.model.Request;
import tarun.djangorestclient.com.djangorestclient.utils.HttpUtil;
import tarun.djangorestclient.com.djangorestclient.utils.MiscUtil;

/**
 * This fragment shows user all necessary fields to make REST requests.
 */
public class RequestFragment extends Fragment implements HeadersRecyclerViewAdapter.HeaderOptionsClickedListener {

    public static final String TITLE = "Request";

    private static final String TAG = RequestFragment.class.getSimpleName();

    private static final int NEW_HEADER_POSITION = -1;

    private String url;
    private String body;

    private EditText etRequestBody;
    private FloatingActionButton addHeaderFab;
    private Spinner methodTypesSpinner;
    private RecyclerView headersRecyclerView;
    private HeadersRecyclerViewAdapter headersRecyclerViewAdapter;

    private Request request;

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

        request = new Request();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_request, container, false);

        etRequestBody = rootView.findViewById(R.id.et_request_body);
        headersRecyclerView = rootView.findViewById(R.id.rv_headers);
        addHeaderFab = rootView.findViewById(R.id.fab_addHeader);
        methodTypesSpinner = rootView.findViewById(R.id.spinner_request_types);

        headersRecyclerViewAdapter = new HeadersRecyclerViewAdapter(this, request.getHeaders());

        bindViews();

        return rootView;
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

        addHeaderFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayAddHeaderDialog();
            }
        });
        methodTypesSpinner.setOnItemSelectedListener(getRequestTypesSpinnerListener());
    }

    private AdapterView.OnItemSelectedListener getRequestTypesSpinnerListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                /*
                  Show/hide request body area based on the request type chosen by user.
                 */
                if (TextUtils.equals(((String) parent.getItemAtPosition(position)), getString(R.string.get))) {
                    etRequestBody.setVisibility(View.GONE);
                } else {
                    etRequestBody.setVisibility(View.VISIBLE);
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
                MiscUtil.showOrHideKeyboard(getContext(), getActivity().getCurrentFocus(), false);
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
                        Toast.makeText(getContext(), R.string.input_fields_empty_msg, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), R.string.input_fields_empty_msg, Toast.LENGTH_SHORT).show();
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
                MiscUtil.showOrHideKeyboard(getContext(), getActivity().getCurrentFocus(), false);
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
                Log.e(TAG," : Unidentified Header type : " + headerTypeString);
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
     * @return: The newly created Header object.
     */
    private Header getNewHeader(HeaderType headerType, String userInput1) {
        return new Header(headerType, userInput1);
    }

    /**
     * Create a new header object based on user provided info.
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
    public void onDeleteHeaderClicked(int position) {
        // Delete the header at position received and update the recycler view list.
        request.getHeaders().remove(position);
        headersRecyclerView.removeViewAt(position);
        headersRecyclerViewAdapter.notifyItemRemoved(position);
        headersRecyclerViewAdapter.notifyItemRangeChanged(position, request.getHeaders().size());
    }

    @Override
    public void onEditHeaderClicked(int position) {
        displayEditHeaderDialog(position);
    }

}
