package com.organizationiworkfor.ribbit.UI;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
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

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Vivien on 10/2/2015.
 */
public class FriendsFragment extends Fragment {
    private static final String TAG = FriendsFragment.class.getSimpleName();
    @Bind(R.id.progressBar2) ProgressBar mProgressBar;
    @Bind(R.id.friendsGrid) GridView mGridView;
    @Bind(android.R.id.empty) TextView emptyTextView;
    private List<ParseUser> mFriends;
    private ParseRelation<ParseUser> mFriendRelation;
    private ParseUser mCurrentUser;

    public FriendsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_grid, container, false);
        ButterKnife.bind(this, rootView);
        mGridView.setEmptyView(emptyTextView);
        return rootView;
    }

    public void onResume() {
        super.onResume();
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendRelation = mCurrentUser.getRelation(ParseConstants.Key_FRIEND_RELATIONS);

        //find friends in the friend relation column
        ParseQuery query = mFriendRelation.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                if (e == null) {
                    mFriends = list;
                    String[] users = new String[mFriends.size()];
                    int i = 0;
                    for (ParseUser user : mFriends) {
                        users[i] = user.getUsername();
                        i++;
                    }

                    if (mGridView.getAdapter() == null) {
                        FriendsAdapter adapter = new FriendsAdapter(getActivity(), mFriends);
                        mGridView.setAdapter(adapter);
                    } else {
                        //position is saved everytime something is clicked
                        ((FriendsAdapter)mGridView.getAdapter()).refill(mFriends);
                    }

                } else {
                    AlertDialogFragment dialog = new AlertDialogFragment();
                    dialog.setAlertMessage(e.getMessage());
                    dialog.show(getActivity().getFragmentManager(), "error with Parse");
                }
            }
        });
    }

}

