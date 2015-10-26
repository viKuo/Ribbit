package com.organizationiworkfor.ribbit.UI;

import android.app.ListActivity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.organizationiworkfor.ribbit.AlertDialogFragment;
import com.organizationiworkfor.ribbit.FileHelper;
import com.organizationiworkfor.ribbit.ParseConstants;
import com.organizationiworkfor.ribbit.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RecipientActivity extends AppCompatActivity {
    private static final String TAG = RecipientActivity.class.getSimpleName();
    @Bind(R.id.progressBar) ProgressBar mProgressBar;
    @Bind(R.id.friendList) ListView mFriendList;
    @Bind(R.id.emptyRecipListLabel) TextView mEmptyLabel;
    private List<ParseUser> mFriends;
    private ParseRelation<ParseUser> mFriendRelation;
    private ParseUser mCurrentUser;
    private MenuItem mSendButton;
    private Uri mUri;
    private String mFileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient);
        ButterKnife.bind(this);
        mFriendList.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mFriendList.setEmptyView(mEmptyLabel);

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
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            RecipientActivity.this,
                            android.R.layout.simple_list_item_checked,
                            users);
                    mFriendList.setAdapter(adapter);
                } else {
                    AlertDialogFragment dialog = new AlertDialogFragment();
                    dialog.setAlertMessage(e.getMessage());
                    dialog.show(getFragmentManager(), "error with Parse");
                }
            }
        });

        mFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mFriendList.getCheckedItemCount() > 0) {
                    mSendButton.setVisible(true);
                } else {
                    mSendButton.setVisible(false);
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
                } else {
                    AlertDialogFragment errorDialog = new AlertDialogFragment();
                    errorDialog.setAlertMessage(getString(R.string.message_upload_error));
                    errorDialog.show(getFragmentManager(), "upload message error");
                }
            }
        });
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
        for (int i = 0; i < mFriendList.getCount(); i++) {
            if (mFriendList.isItemChecked(i)) {
                recipientIds.add(mFriends.get(i).getObjectId());
            }
        }
        return recipientIds;
    }
}
