package com.uic.amrish.fedcash;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.uic.amrish.common.DataSharingUtility;
import com.uic.amrish.common.R;

/**
 * Created by Amrish on 02-Dec-17.
 */

public class LeftCallsFragment extends ListFragment {

    private ListSelectionListener mListener = null;

    /**
     * This is interface which is implemented by the activity. So the events can be sent to the activity.
     */
    public interface ListSelectionListener {
        public void onListSelection(int index);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {

            // The activity is attached to this reference since the interface is implemented by it.
            mListener = (ListSelectionListener) context;

        } catch (ClassCastException e) {
            throw e;
        }
    }


    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setting the ListAdaptor with the context, layout and the values.
        setListAdapter(new ArrayAdapter<String>(getActivity(), R.layout.list_item, DataSharingUtility.getAllServiceCalls()));

        //Single choice mode list
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        //Once the item in the list is clicked, it is highlighted.
        getListView().setItemChecked(position, true);

        //Also inform the activity so appropriate action can be taken.
        mListener.onListSelection(position);
    }

    public void deselectChoice(int position) {

        //De select the item in the list at the position.
        getListView().setItemChecked(position, false);
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {


        //To retain the fragment across device orientations
        setRetainInstance(true);

        super.onCreate(savedInstanceState);
    }
}
