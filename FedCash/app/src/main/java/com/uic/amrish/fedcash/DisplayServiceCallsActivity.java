package com.uic.amrish.fedcash;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.uic.amrish.common.R;

public class DisplayServiceCallsActivity extends Activity implements LeftCallsFragment.ListSelectionListener {

    /**
     * Required for working with fragments and framelayouts
     */
    private FrameLayout mLeftFragmentLayout;
    private FragmentManager mFragmentManager;
    private RightResponseFragment mRightFragment;
    private LeftCallsFragment mListFragment;

    /**
     * Required for invoking the deselection on the list fragment
     */
    private static int currentSelection = -1;

    /**
     * TAG for retaining the web view fragment
     */
    private static final String TAG_RETAINED_FRAGMENT_RIGHT = "RetainedRightFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_service_calls);

        //Retrieve the framelayout
        mLeftFragmentLayout = (FrameLayout) findViewById(R.id.right_fragment);

        // get the fragemnt manager
        mFragmentManager = getFragmentManager();

        //Retrieve the static fragment
        mListFragment = (LeftCallsFragment) mFragmentManager.findFragmentById(R.id.left_fragment);

        //Retrieve the dynamic fragment by TAG
        mRightFragment = (RightResponseFragment) mFragmentManager.findFragmentByTag(TAG_RETAINED_FRAGMENT_RIGHT);

        // If the retained fragment is null, then just initialize it
        if (mRightFragment == null) {
            //Toast.makeText(getApplicationContext(), "webview is null", Toast.LENGTH_SHORT).show();
            mRightFragment = new RightResponseFragment();
        } else {
            // else the fragment should be visible in the new orientation, so set the layout
            setLayout();
            //load the url of the page
            mRightFragment.showNewTextView(mRightFragment.getCurrentTextView());
        }

        //Add a OnBackStackChangedListener to reset the layout when the back stack changes
        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                setLayout();
            }
        });

    }

    private void setLayout() {

        //if landscape mode do some changes
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            //if webview fragment is added then show the list & webview in 1:2 ratio of the screen
            if (mRightFragment.isAdded()) {
                //Toast.makeText(getApplicationContext(), "back listener", Toast.LENGTH_SHORT).show();
                mLeftFragmentLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));
                mListFragment.getView().setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

            } else {
                //if web view is not added then show just the list fragment
                mLeftFragmentLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
            }
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            //Do Changes if the orientation is portrait

            // only show the web view fragment if it is added
            if (mRightFragment.isAdded()) {
                //Toast.makeText(getApplicationContext(), "back listener", Toast.LENGTH_SHORT).show();
                mLeftFragmentLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f));
                mListFragment.getView().setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0f));

            } else {
                //else just show the list fragment
                mLeftFragmentLayout.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 0));
                mListFragment.getView().setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            }

        }
        //Toast.makeText(getApplicationContext(), mListFragment.isAdded() +" " + !mRightFragment.isAdded(), Toast.LENGTH_SHORT).show();
        //If the list fragment is added and webview is not , then deselect the choice
        if (mListFragment.isAdded() && !mRightFragment.isAdded()) {
            //Toast.makeText(getApplicationContext(), "back stack", Toast.LENGTH_SHORT).show();
            mListFragment.deselectChoice(currentSelection);
        }
    }


    @Override
    /*
    Called from the list fragment when a list item is selected. So the activity can take appropriate action such as passing the information to fragment2
     */
    public void onListSelection(int index) {

        // if webview fragment is not added then add it and put it on the back stack
        if (!mRightFragment.isAdded()) {
            //Toast.makeText(getApplicationContext(), "" + index, Toast.LENGTH_SHORT).show();

            // get the fragment transcation from the fragment manager
            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

            //add the fragment to the framelayout with a TAG
            mFragmentTransaction.add(R.id.right_fragment, mRightFragment, TAG_RETAINED_FRAGMENT_RIGHT);
            //mFragmentTransaction.replace(R.id.landmark_webview_fragment_container, mRightFragment);

            //put the current transcation on a back stack.
            mFragmentTransaction.addToBackStack(null);

            //commit the transaction
            mFragmentTransaction.commit();

            // Force Android to execute the committed FragmentTransaction
            mFragmentManager.executePendingTransactions();
        }

        // If the current index shown and the selected index are separate then show the webpage of the new index.
        if (index != mRightFragment.getCurrentTextView()) {
            //Toast.makeText(getApplicationContext(), "if: " + index, Toast.LENGTH_SHORT).show();
            setLayout();
            currentSelection = index;
            mRightFragment.showNewTextView(index);
        }
    }
}
