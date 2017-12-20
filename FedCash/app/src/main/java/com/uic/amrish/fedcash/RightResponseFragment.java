package com.uic.amrish.fedcash;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uic.amrish.common.DataSharingUtility;

/**
 * Created by Amrish on 03-Dec-17.
 */

public class RightResponseFragment extends Fragment {


    private TextView textView;
    private int currentTextView = -1;


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        textView=new TextView(getActivity());

        if (currentTextView != -1) {
            showNewTextView(currentTextView);
        }


        //to retian this fragment across the orientation changes
        setRetainInstance(true);
        return textView;
    }

    public void showNewTextView(int index) {

        /**
         * If the indices are within a valid range then load the new url
         */
        if (index >= 0 && index < DataSharingUtility.getAllServiceCalls().length) {
            currentTextView = index;
            textView.setText(DataSharingUtility.getServiceCallResponse(index));
        }
    }

    /**
     * Return the index of the current page being shown
     * @return int
     */
    public int getCurrentTextView() {
        return currentTextView;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
