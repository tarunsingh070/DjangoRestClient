package tarun.djangorestclient.com.djangorestclient.fragment;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import tarun.djangorestclient.com.djangorestclient.R;

/**
 * This fragment shows user all necessary fields to make REST requests.
 */
public class RequestFragment extends Fragment {

    public static final String TITLE = "Request";

    private static final String TAG = RequestFragment.class.getSimpleName();

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_request, container, false);

        FloatingActionButton addHeaderFab = rootView.findViewById(R.id.fab_addHeader);
        addHeaderFab.setOnClickListener(getAddHeaderFabClickListener());

        return rootView;
    }

    private View.OnClickListener getAddHeaderFabClickListener() {

        return new View.OnClickListener() {

            TextView tvHeaderLabel1 = null;
            TextView tvHeaderLabel2 = null;
            LinearLayout layoutHeaderFields2 = null;

            @Override
            public void onClick(View v) {
                // Inflate dialog_add_header.xml
                LayoutInflater li = LayoutInflater.from(getContext());
                View addHeaderDialogView = li.inflate(R.layout.dialog_add_header, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getContext());

                // set our custom inflated view to alertdialog builder.
                alertDialogBuilder.setView(addHeaderDialogView);

                // create alert dialog
                final AlertDialog alertDialog = alertDialogBuilder.create();

                // Initialize views.
                final Spinner headerTypesSpinner = addHeaderDialogView.findViewById(R.id.spinner_header_types);
                final EditText userInput1 = addHeaderDialogView.findViewById(R.id.et_header_value_1);
                final EditText userInput2 = addHeaderDialogView.findViewById(R.id.et_header_value_2);
                tvHeaderLabel1 = addHeaderDialogView.findViewById(R.id.tv_header_label_1);
                tvHeaderLabel2 = addHeaderDialogView.findViewById(R.id.tv_header_label_2);
                layoutHeaderFields2 = addHeaderDialogView.findViewById(R.id.layout_header_fields_2);
                final Button okButton = addHeaderDialogView.findViewById(R.id.button_ok);
                final Button cancelButton = addHeaderDialogView.findViewById(R.id.button_cancel);

                // Bind views.
                headerTypesSpinner.setOnItemSelectedListener(getHeaderTypesSpinnerListener());
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addHeader();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.show();
            }

            private AdapterView.OnItemSelectedListener getHeaderTypesSpinnerListener() {
                return new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        /**
                         * Show/hide certain views based on the header type chosen by user.
                         */
                        if (TextUtils.equals(((String) parent.getItemAtPosition(position)), getString(R.string.auth_basic))) {
                            layoutHeaderFields2.setVisibility(View.VISIBLE);
                            tvHeaderLabel1.setText(R.string.username);
                            tvHeaderLabel2.setText(R.string.password);
                        } else if (TextUtils.equals(((String) parent.getItemAtPosition(position)), getString(R.string.custom))) {
                            layoutHeaderFields2.setVisibility(View.VISIBLE);
                            tvHeaderLabel1.setText(R.string.header_name);
                            tvHeaderLabel2.setText(R.string.header_value);
                        } else {
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
        };
    }

    private void addHeader() {
        // Todo: Implement functionality to add header.
    }

}
