package tarun.djangorestclient.com.djangorestclient.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tarun.djangorestclient.com.djangorestclient.R;

/**
 * Dummy fragment for making the REST requests.
 */
// TODO: Implement functionality.
public class RequestFragment extends Fragment {

    public static final String TITLE = "Request";

    private static final String TAG = RequestFragment.class.getSimpleName();

    public RequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
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
        return inflater.inflate(R.layout.fragment_request, container, false);
    }

}
