package com.organizationiworkfor.ribbit.UI;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.organizationiworkfor.ribbit.AlertDialogFragment;
import com.organizationiworkfor.ribbit.ParseConstants;
import com.organizationiworkfor.ribbit.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditFriendsActivity extends ListActivity {
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    private List<ParseUser> mUsers;
    private ParseRelation<ParseUser> mFriendRelation;
    private ParseUser mCurrentUser;
    private final static String TAG = EditFriendsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friends);
        ButterKnife.bind(this);
        getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendRelation = mCurrentUser.getRelation(ParseConstants.Key_FRIEND_RELATIONS);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseUser>() {

            @Override
            public void done(List<ParseUser> list, ParseException e) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (e == null) {
                    //success!
                    mUsers = list;
                    String[] users = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        users[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            EditFriendsActivity.this,
                            android.R.layout.simple_list_item_checked,
                            users);
                    setListAdapter(adapter);

                    addFriendsCheckMark();
                } else {
                    AlertDialogFragment dialog = new AlertDialogFragment();
                    dialog.setAlertMessage(getString(R.string.generic_error));
                    dialog.show(getFragmentManager(), "Error Dialog");
                }

            }
        });
    }

    private void addFriendsCheckMark() {
        mFriendRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    for (int i = 0 ; i < mUsers.size(); i++) {
                        for (ParseUser friend : list) {
                            if (friend.getObjectId().equals(mUsers.get(i).getObjectId())){
                                getListView().setItemChecked(i,true);
                            }
                        }
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (getListView().isItemChecked(position)) {
            //user just clicked on this item therefore from notChecked to checked
            //add friend
            mFriendRelation.add(mUsers.get(position));
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        } else {
            //remove friend
            mFriendRelation.remove(mUsers.get(position));
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });
        }
    }
}
