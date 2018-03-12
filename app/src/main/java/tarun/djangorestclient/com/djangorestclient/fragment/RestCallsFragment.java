package tarun.djangorestclient.com.djangorestclient.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tarun.djangorestclient.com.djangorestclient.adapter.RequestResponsePagerAdapter;
import tarun.djangorestclient.com.djangorestclient.R;

/**
 * This fragment shows the Request and Response screens in a tabular fashion for the user to make requests and view responses.
 */
public class RestCallsFragment extends Fragment {

    private static final String TAG = RestCallsFragment.class.getSimpleName();

    public RestCallsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
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

        // Pass in the childFragmentManager instead of the fragmentManager because here we have a
        // view pager based fragment showing nested fragments into it.
        RequestResponsePagerAdapter requestResponsePagerAdapter = new RequestResponsePagerAdapter(getChildFragmentManager());

        ViewPager restViewPager = rootView.findViewById(R.id.restViewPager);
        restViewPager.setAdapter(requestResponsePagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = rootView.findViewById(R.id.layout_tabs);
        tabLayout.setupWithViewPager(restViewPager);

        return rootView;
    }

}
