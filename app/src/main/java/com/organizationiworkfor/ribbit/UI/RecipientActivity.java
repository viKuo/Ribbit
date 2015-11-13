package com.organizationiworkfor.ribbit.UI;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.organizationiworkfor.ribbit.Adapters.FriendsAdapter;
import com.organizationiworkfor.ribbit.AlertDialogFragment;
import com.organizationiworkfor.ribbit.R;
import com.organizationiworkfor.ribbit.Utils.FileHelper;
import com.organizationiworkfor.ribbit.Utils.ParseConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecipientActivity extends AppCompatActivity {
    private static final String TAG = RecipientActivity.class.getSimpleName();
    @Bind(R.id.progressBar2) ProgressBar mProgressBar;
    @Bind(R.id.friendsGrid) GridView mGridView;
    @Bind(android.R.id.empty) TextView emptyTextView;
    private List<ParseUser> mFriends;
    private ParseRelation<ParseUser> mFriendRelation;
    private ParseUser mCurrentUser;
    private MenuItem mSendButton;
    private Uri mUri;
    private String mFileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_grid);
        ButterKnife.bind(this);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        emptyTextView.setText(getString(R.string.choose_friend_label));
        mGridView.setEmptyView(emptyTextView);

        mUri = getIntent().getData();
        mFileType = getIntent().getStringExtra(ParseConstants.KEY_FILE_TYPE);
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
                        FriendsAdapter adapter = new FriendsAdapter(RecipientActivity.this, mFriends);
                        mGridView.setAdapter(adapter);
                    } else {
                        //position is saved everytime something is clicked
                        ((FriendsAdapter)mGridView.getAdapter()).refill(mFriends);
                    }
                } else {
                    AlertDialogFragment dialog = new AlertDialogFragment();
                    dialog.setAlertMessage(e.getMessage());
                    dialog.show(getFragmentManager(), "error with Parse");
                }
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView checkedImageView = (ImageView) view.findViewById(R.id.userImageChecked);
                if (mGridView.getCheckedItemCount() > 0) {
                    mSendButton.setVisible(true);
                } else {
                    mSendButton.setVisible(false);
                }

                if(mGridView.isItemChecked(position)) {
                    checkedImageView.setVisibility(View.VISIBLE);
                } else {
                    checkedImageView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipient, menu);
        mSendButton = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //only item to select is the send button
        ParseObject message = createMessage();
        if (message == null) {
            AlertDialogFragment errorDialog = new AlertDialogFragment();
            errorDialog.setAlertMessage(getString(R.string.message_file_error));
            errorDialog.show(getFragmentManager(), "File chosen error");
        } else {
            send(message);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void send(ParseObject message) {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(RecipientActivity.this, R.string.message_success, Toast.LENGTH_LONG).show();
                    sendPushNotification();
                } else {
                    AlertDialogFragment errorDialog = new AlertDialogFragment();
                    errorDialog.setAlertMessage(getString(R.string.message_upload_error));
                    errorDialog.show(getFragmentManager(), "upload message error");
                }
            }
        });
    }

    private void sendPushNotification() {
        ParseQuery<ParseInstallation> pushQuery = ParseInstallation.getQuery();
        pushQuery.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipients());

        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setMessage(getString(R.string.push_message,ParseUser.getCurrentUser().getUsername()));
        push.sendInBackground();
    }

    private ParseObject createMessage() {
        ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGE);
        message.put(ParseConstants.KEY_SENDER_ID, mCurrentUser.getObjectId());
        message.put(ParseConstants.KEY_SENDER_NAME, mCurrentUser);
        message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
        message.put(ParseConstants.KEY_FILE_TYPE, mFileType);
        byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mUri);
        if (fileBytes == null) {
            return null;
        } else {
            if (mFileType == ParseConstants.IMAGE) {
                fileBytes = FileHelper.reduceImageForUpload(fileBytes);
            }
            String fileName = FileHelper.getFileName(this, mUri, mFileType);
            ParseFile file = new ParseFile(fileName, fileBytes);
            message.put(ParseConstants.KEY_FILE, file);
            return message;
        }
    }

    private ArrayList<String> getRecipientIds() {
        ArrayList<String> recipientIds = new ArrayList<String>();
        for (int i = 0; i < mGridView.getCount(); i++) {
            if (mGridView.isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }

    private List<ParseUser> getRecipients() {
        List<ParseUser> recipients = new ArrayList<ParseUser>();
        for (int i = 0; i < mGridView.getCount(); i++) {
            if (mGridView.isItemChecked(i)) {
                recipients.add(mFriends.get(i));
            }
        }
        return recipients;
    }
}
