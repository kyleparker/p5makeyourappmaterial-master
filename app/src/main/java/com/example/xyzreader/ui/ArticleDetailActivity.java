package com.example.xyzreader.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.transition.TransitionInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.object.Article;
import com.example.xyzreader.utils.Constants;
import com.example.xyzreader.utils.UIUtils;
import com.squareup.picasso.Picasso;

/**
 * An activity representing a single Article detail screen, letting you swipe between articles.
 */
public class ArticleDetailActivity extends BaseActivity {
    private Article mArticle;

    private int mArticleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        setupToolbar();
        getExtras();
        setupFAB();
        getArticleInfo();

        if (UIUtils.isLollipop()) {
            mActivity.getWindow().setSharedElementEnterTransition(TransitionInflater.from(mActivity)
                    .inflateTransition(R.transition.move));
        }
    }

    /**
     * Retrieve the article info
     */
    private void getArticleInfo() {
        Runnable load = new Runnable() {
            public void run() {
                try {
                    if (mArticle == null) {
                        mArticle = mProvider.getArticle(mArticleId);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        mActivity.runOnUiThread(getArticleInfoRunnable);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };

        Thread thread = new Thread(null, load, "getArticleInfo");
        thread.start();
    }

    private final Runnable getArticleInfoRunnable = new Runnable() {
        public void run() {
            if (mArticle != null) {
                CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                collapsingToolbarLayout.setTitle(mArticle.getTitle());

                ImageView image = (ImageView) findViewById(R.id.article_image);

                Picasso.with(mActivity)
                        .load(mArticle.getPhotoUrl())
                        .fit()
                        .centerCrop()
                        .into(image);

                ((TextView) findViewById(R.id.author)).setText(mArticle.getAuthor());
                ((TextView) findViewById(R.id.date))
                        .setText(UIUtils.getDateString(mActivity, mArticle.getPublishedDate(), Constants.FULL_DATE_FORMAT));

                TextView textView = (TextView) findViewById(R.id.body);
                textView.setClickable(true);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setText(Html.fromHtml(mArticle.getBody()));
            }
        }
    };

    /**
     * Get extras from the intent bundle
     */
    private void getExtras() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            mArticleId = extras.getInt(Constants.EXTRA_ARTICLE_ID, -1);
        }
    }

    private void setupFAB() {
        final FloatingActionButton fabShare = (FloatingActionButton) findViewById(R.id.fab_share);
        fabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mArticle != null) {
                    // DONE: Share the article via intent
                    String subject = mActivity.getString(R.string.content_share_subject, mArticle.getTitle());
                    String message = mActivity.getString(R.string.content_share_message, mArticle.getAuthor(), mArticle.getBody());

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/html");
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(message));

                    mActivity.startActivity(Intent.createChooser(intent, mActivity.getString(R.string.content_select_app)));
                }
            }
        });

        if (UIUtils.isLollipop()) {
            fabShare.setOnTouchListener(UIUtils.getFABTouchListener(mActivity, fabShare));
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_up);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
