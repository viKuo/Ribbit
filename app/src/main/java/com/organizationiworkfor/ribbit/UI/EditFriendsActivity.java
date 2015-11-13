package com.organizationiworkfor.ribbit.UI;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.organizationiworkfor.ribbit.Adapters.FriendsAdapter;
import com.organizationiworkfor.ribbit.AlertDialogFragment;
import com.organizationiworkfor.ribbit.R;
import com.organizationiworkfor.ribbit.Utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditFriendsActivity extends Activity {
    @Bind(R.id.progressBar2) ProgressBar mProgressBar;
    @Bind(R.id.friendsGrid) GridView mGridView;
    @Bind(android.R.id.empty) TextView emptyTextView;
    private List<ParseUser> mUsers;
    private ParseRelation<ParseUser> mFriendRelation;
    private ParseUser mCurrentUser;
    private final static String TAG = EditFriendsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);
        ButterKnife.bind(this);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        emptyTextView.setText(getString(R.string.generic_error));
        mGridView.setEmptyView(emptyTextView);
        mGridView.setOnItemClickListener(mOnItemClickListener);
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

                    if (mGridView.getAdapter() == null) {
                        FriendsAdapter adapter = new FriendsAdapter(EditFriendsActivity.this, mUsers);
                        mGridView.setAdapter(adapter);
                    } else {
                        //position is saved every time something is clicked
                        ((FriendsAdapter)mGridView.getAdapter()).refill(mUsers);
                    }

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
                                mGridView.setItemChecked(i,true);
                                //calls the adapter, hence adding checkmarks
                            }
                        }
                    }
                } else {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //view passed in here is the relative layout of the item, aka FriendsAdapter.java/user_grid_item.xml
            ImageView checkedImageView = (ImageView) view.findViewById(R.id.userImageChecked);
            if (mGridView.isItemChecked(position)) {
                //user just clicked on this item therefore from notChecked to checked
                //add friend
                mFriendRelation.add(mUsers.get(position));
                checkedImageView.setVisibility(View.VISIBLE);
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
                checkedImageView.setVisibility(View.INVISIBLE);
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
    };

}
