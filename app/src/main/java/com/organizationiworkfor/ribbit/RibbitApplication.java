package com.organizationiworkfor.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Vivien on 10/1/2015.
 */
public class RibbitApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "SpiCfFQnM9ZC8ygKC0fCmZFtmwyqnMmzJR9JPXS7", "VXI9G1CQ0Bv5fntW5TO7QaF3fJfkyxt3v4bq5wjl");

    }

}
