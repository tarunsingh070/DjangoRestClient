package tarun.djangorestclient.com.djangorestclient.fragment;


import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.model.RestResponse;

/**
 * This fragment shows user the response information received as a result of the REST request made by user.
 */
public class ResponseFragment extends Fragment {

    public static final String TITLE = "Response";

    private static final String TAG = ResponseFragment.class.getSimpleName();

    private TextView tvResponseCode;
    private TextView tvResponseTime;
    private TextView tvResponseBody;
    private TextView tvRequestUrl;
    private TextView tvResponseHeaders;

    private RestResponse restResponse;

    public ResponseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment ResponseFragment.
     */
    public static ResponseFragment newInstance() {
        return new ResponseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_response, container, false);

        // Initialize views.
        tvResponseCode = rootView.findViewById(R.id.tv_response_code);
        tvResponseTime = rootView.findViewById(R.id.tv_response_time);
        tvResponseBody = rootView.findViewById(R.id.tv_body);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.response_fragment_menu, menu);
        MenuItem item = menu.findItem(R.id.action_show_extra_info);
        item.setVisible(restResponse != null);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_show_extra_info:
                showAdditionalResponseInfo(restResponse.getUrl(), restResponse.getResponseHeaders());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Update the response screen with the response info received.
     */
    public void updateUI(RestResponse restResponse) {
        this.restResponse = restResponse;
        getActivity().invalidateOptionsMenu();
        tvResponseCode.setText(getString(R.string.response_code_label_with_value, restResponse.getResponseCode()));
        tvResponseTime.setText(getString(R.string.response_time_ms_label_with_value, restResponse.getResponseTime()));
        tvResponseBody.setText(restResponse.getResponseBody());
    }

    /**
     * Show the additional response information inside a bottom sheet dialog.
     */
    private void showAdditionalResponseInfo(String requestUrl, CharSequence responseHeaders) {
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_response_info, null);

        tvRequestUrl = view.findViewById(R.id.tv_request_url);
        tvResponseHeaders = view.findViewById(R.id.tv_headers);

        tvRequestUrl.setText(requestUrl);
        tvResponseHeaders.setText(responseHeaders);

        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        dialog.setContentView(view);
        dialog.show();
    }

}
