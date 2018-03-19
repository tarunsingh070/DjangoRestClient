package tarun.djangorestclient.com.djangorestclient.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tarun.djangorestclient.com.djangorestclient.R;
import tarun.djangorestclient.com.djangorestclient.adapter.RequestResponsePagerAdapter;
import tarun.djangorestclient.com.djangorestclient.model.RestResponse;

/**
 * This fragment shows the Request and Response screens in a tabular fashion for the user to make requests and view responses.
 */
public class RestCallsFragment extends Fragment {

    private static final String TAG = RestCallsFragment.class.getSimpleName();

    private TabLayout tabLayout;

    private RequestFragment requestFragment;
    private ResponseFragment responseFragment;

    public RestCallsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment RestCallsFragment.
     */
    public static RestCallsFragment newInstance() {
        return new RestCallsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_rest_calls, container, false);

        requestFragment = RequestFragment.newInstance();
        responseFragment = ResponseFragment.newInstance();

        // Pass in the childFragmentManager instead of the fragmentManager because here we have a
        // view pager based fragment showing nested fragments into it.
        RequestResponsePagerAdapter requestResponsePagerAdapter
                = new RequestResponsePagerAdapter(getChildFragmentManager(), requestFragment, responseFragment);

        ViewPager restViewPager = rootView.findViewById(R.id.restViewPager);
        restViewPager.setAdapter(requestResponsePagerAdapter);

        // Give the TabLayout the ViewPager
        tabLayout = rootView.findViewById(R.id.layout_tabs);
        tabLayout.setupWithViewPager(restViewPager);

        return rootView;
    }

    /**
     * Switch to response screen tab and update the Response screen UI with info present in restResponse object.
     *
     * @param restResponse: RestResponse object containing Response information from RequestScreen.
     */
    public void switchToResponseScreenTab(RestResponse restResponse) {
        tabLayout.getTabAt(1).select();
        responseFragment.updateUI(restResponse);
    }

}
