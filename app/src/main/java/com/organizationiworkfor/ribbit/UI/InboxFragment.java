package com.organizationiworkfor.ribbit.UI;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.organizationiworkfor.ribbit.Adapters.MessageAdapter;
import com.organizationiworkfor.ribbit.AlertDialogFragment;
import com.organizationiworkfor.ribbit.R;
import com.organizationiworkfor.ribbit.Utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vivien on 10/2/2015.
 */
public class InboxFragment extends ListFragment {
    private List<ParseObject> mMessages;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    public InboxFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swipe_refresh1,
                R.color.swipe_refresh2,
                R.color.swipe_refresh3,
                R.color.swipe_refresh4);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMessages();
    }

    private void getMessages() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGE);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());

        query.orderByDescending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (mSwipeRefreshLayout.isRefreshing()) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    mMessages = objects;
                    if (getListAdapter() == null) {
                        MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                        setListAdapter(adapter);
                    } else
                        ((MessageAdapter)getListAdapter()).refill(mMessages);
                } else {
                    AlertDialogFragment dialogFragment = new AlertDialogFragment();
                    dialogFragment.setAlertMessage(e.getMessage());
                    dialogFragment.show(getActivity().getFragmentManager(), "error with messages query");
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String fileType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri uri = Uri.parse(file.getUrl());

        if (fileType.equals(ParseConstants.IMAGE)) {
            //view image here
            Intent intent = new Intent(getListView().getContext(), ViewImageActivity.class);
            intent.setData(uri);
            startActivity(intent);
        } else {
            //view video here
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setDataAndType(uri, "video/*");
            startActivity(intent);
        }
        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);
        if (ids.size() == 1) {
            message.deleteInBackground();
        } else {
            ids.remove(ParseUser.getCurrentUser().getObjectId());

            ArrayList<String> idsToRemove = new ArrayList<String>();
            message.removeAll(ParseUser.getCurrentUser().getObjectId(), idsToRemove);
            message.saveInBackground();
        }
    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            getMessages();
        }
    };
}

