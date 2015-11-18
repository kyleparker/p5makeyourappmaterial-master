package com.example.xyzreader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.xyzreader.R;
import com.example.xyzreader.data.AppProviderUtils;

/**
 *
 * Created by kyleparker on 11/5/2015.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected static AppCompatActivity mActivity;
    protected static AppProviderUtils mProvider;

    private Toolbar mActionBarToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = this;

        mProvider = AppProviderUtils.Factory.get(mActivity);
    }

    /**
     * Retrieve the base toolbar for the activity.
     *
     * @return
     */
    protected Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }

        return mActionBarToolbar;
    }

    /**
     * Converts a fragment arguments bundle into an intent.
     */
    public static Intent fragmentArgumentsToIntent(Bundle arguments) {
        Intent intent = new Intent();
        if (arguments == null) {
            return intent;
        }

        intent.putExtras(arguments);
        return intent;
    }
}
