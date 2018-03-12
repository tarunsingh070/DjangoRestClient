package tarun.djangorestclient.com.djangorestclient.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tarun.djangorestclient.com.djangorestclient.R;

/**
 * Dummy fragment for displaying the REST response.
 */
// TODO: Implement functionality.
public class ResponseFragment extends Fragment {

    public static final String TITLE = "Response";

    private static final String TAG = ResponseFragment.class.getSimpleName();

    public ResponseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     * @return A new instance of fragment ResponseFragment.
     */
    public static ResponseFragment newInstance() {
        return new ResponseFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_response, container, false);
    }

}
