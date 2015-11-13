package com.organizationiworkfor.ribbit;

import android.app.Application;

import com.organizationiworkfor.ribbit.Utils.ParseConstants;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

/**
 * Created by Vivien on 10/1/2015.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "SpiCfFQnM9ZC8ygKC0fCmZFtmwyqnMmzJR9JPXS7", "VXI9G1CQ0Bv5fntW5TO7QaF3fJfkyxt3v4bq5wjl");
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

    public static void updateParseInstallation(ParseUser user){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(ParseConstants.KEY_USER_ID, user);
        installation.saveInBackground();
    }

}
