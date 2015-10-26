package com.organizationiworkfor.ribbit.UI;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.organizationiworkfor.ribbit.Adapters.MessageAdapter;
import com.organizationiworkfor.ribbit.AlertDialogFragment;
import com.organizationiworkfor.ribbit.ParseConstants;
import com.organizationiworkfor.ribbit.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Vivien on 10/2/2015.
 */
public class InboxFragment extends ListFragment {
    private List<ParseObject> mMessages;

    public InboxFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSAGE);

        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.orderByDescending(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    mMessages = objects;
                    String[] users = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages) {
                        users[i] = message.getParseObject(ParseConstants.KEY_SENDER_NAME).getString(ParseConstants.KEY_USERNAME);
                        i++;
                    }
                    MessageAdapter adapter = new MessageAdapter(getListView().getContext(), mMessages);
                    setListAdapter(adapter);
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
        }

    }
}
