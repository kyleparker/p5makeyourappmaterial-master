package com.example.xyzreader.ui;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.test.UiThreadTest;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.xyzreader.R;
import com.example.xyzreader.object.Article;
import com.example.xyzreader.service.ArticleService;
import com.example.xyzreader.utils.Adapters;
import com.example.xyzreader.utils.Constants;
import com.example.xyzreader.utils.DialogUtils;
import com.example.xyzreader.utils.SharedPreferencesHelper;
import com.example.xyzreader.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

// DONE: App uses the Design Support library and its provided widget types (FloatingActionButton, AppBarLayout, SnackBar, etc).
// DONE: App uses CoordinatorLayout for the main activity.
// DONE: App theme extends from AppCompat.
// DONE: App uses an app bar and associated toolbars.
// DONE: App provides a Floating Action Button (FAB) for the most common action(s).
// DONE: App properly specifies elevations for app bars, FABs, and other elements specified in the Material Design specification.
// DONE: App provides sufficient space between text and surrounding elements.
// DONE: App has a consistent color theme defined in styles.xml.
// DONE: Color theme does not impact usability of the app.
// DONE: App uses images that are high quality, specific, and full bleed.
// DONE: App uses fonts that are either the Android defaults, are complementary, and aren't otherwise distracting.
// DONE: App conforms to common standards found in the Android Nanodegree General Project Guidelines
// DONE: Remove custom font from header
// DONE: Update list to use RecyclerView and standard layout
// DONE: Update colors of text
// DONE: Make links active
// DONE: Fix article detail
// DONE: Fix action bar - remove image, make a header image and parallax scroll
/**
 * An activity representing a list of Articles. This activity has different presentations for
 * handset and tablet-size devices. On handsets, the activity presents a list of items, which when
 * touched, lead to a {@link ArticleDetailActivity} representing item details. On tablets, the
 * activity presents a grid of items as cards.
 */
public class ArticleListActivity extends BaseActivity {

    private CoordinatorLayout mCoordinatorLayout;
    private SwipeRefreshLayout mSwipeContainer;
    private MaterialDialog mProgressDialog;
    private Snackbar mSnackbar;

    private BroadcastReceiver mCompletedReceiver;

    private Adapters.ItemAdapter mAdapter;
    private List<Article> mArticleList = new ArrayList<>();

    private boolean mFromSwipe = false;
    private boolean mIsRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_list);

        setupToolbar();
        setupView();
        registerReceiver();

        // If a network connection is available, retrieve the list of articles from the backend server
        if (savedInstanceState == null) {
            if (SharedPreferencesHelper.getBoolean(mActivity, R.string.pref_articles_loaded, false)) {
                getArticleList();
            } else {
                loadArticles();
            }
        } else {
            mArticleList = (ArrayList) savedInstanceState.getParcelableArrayList(Constants.EXTRA_ARTICLE_LIST);
            getArticleList();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mArticleList != null && !mArticleList.isEmpty()) {
            outState.putParcelableArrayList(Constants.EXTRA_ARTICLE_LIST, (ArrayList) mArticleList);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }

        if (mCompletedReceiver != null) {
            getApplicationContext().unregisterReceiver(mCompletedReceiver);
        }

        super.onDestroy();
    }

    /**
     * In a separate thread, retrieve the articles from the local database
     */
    private void getArticleList() {
        if (!mFromSwipe) {
            mProgressDialog = DialogUtils.createSpinnerProgressDialog(mActivity, DialogUtils.DEFAULT_TITLE_ID, R.string.dialog_loading, false);
            mProgressDialog.show();
        }

        Runnable load = new Runnable() {
            public void run() {
                try {
                    if (mArticleList == null || mArticleList.isEmpty()) {
                        mArticleList = mProvider.getArticleList();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    mActivity.runOnUiThread(getArticleListRunnable);
                }
            }
        };

        Thread thread = new Thread(null, load, "getArticleList");
        thread.start();
    }

    /**
     * On the main thread, update the adapter and display the list of articles
     */
    private final Runnable getArticleListRunnable = new Runnable() {
        public void run() {
            try {
                if (!mArticleList.isEmpty()) {
                    mAdapter.addAll(mArticleList);
                } else {
                    // showEmptyView();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (!mFromSwipe) {
                mProgressDialog.dismiss();
            }

            // Now we call setRefreshing(false) to signal refresh has finished
            mSwipeContainer.setRefreshing(false);
        }
    };

    private void loadArticles() {
        if (mSnackbar != null) {
            mSnackbar.dismiss();
        }

        if (UIUtils.isOnline(mActivity)) {
            // Show loading spinner, will be dismissed by the broadcast receiver
            mProgressDialog = DialogUtils.createSpinnerProgressDialog(mActivity, DialogUtils.DEFAULT_TITLE_ID,
                    R.string.dialog_loading_articles, false);
            mProgressDialog.show();

            mActivity.startService(new Intent(mActivity, ArticleService.class));
        } else {
            mSnackbar = Snackbar.make(mCoordinatorLayout, R.string.content_device_offline, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.action_settings, mSnackbarOnClickListener);
            mSnackbar.show();
        }
    }

    /**
     * Register the receivers for the activity
     */
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.RECEIVER_ARTICLE_COMPLETED);
        mCompletedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                SharedPreferencesHelper.setBoolean(mActivity, R.string.pref_articles_loaded, true);
                getArticleList();
            }
        };

        getApplicationContext().registerReceiver(mCompletedReceiver, intentFilter);
    }

    /**
     * Setup the toolbar for the activity
     */
    private void setupToolbar() {
        final Toolbar toolbar = getActionBarToolbar();
        toolbar.setBackgroundColor(mActivity.getResources().getColor(R.color.theme_primary));
        toolbar.post(new Runnable() {
            @Override
            public void run() {
                toolbar.setTitle(mActivity.getString(R.string.app_name));
            }
        });
    }

    /**
     * Setup the view for the activity
     */
    private void setupView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.item_list);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity,
                mActivity.getResources().getInteger(R.integer.articles_per_row));
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);

        mAdapter = new Adapters.ItemAdapter(mActivity);
        recyclerView.setAdapter(mAdapter);

        mSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        // Setup refresh listener which triggers new data loading
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here. Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                mFromSwipe = true;
                mArticleList.clear();
                loadArticles();
            }
        });

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
    }

    /**
     * Handle the click listener for the snackbar "undo" action
     */
    private final View.OnClickListener mSnackbarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActivity.startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
        }
    };
}
