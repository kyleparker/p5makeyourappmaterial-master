package com.example.xyzreader.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.xyzreader.R;
import com.example.xyzreader.object.Article;
import com.example.xyzreader.ui.ArticleDetailActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * View adapters
 *
 * Created by kyleparker on 11/5/2015.
 */
public class Adapters {
    public static class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

        private Activity mContext;
        private List<Article> mArticles;
        private OnItemClickListener mOnItemClickListener;
        private int mPreviousPosition;

        public ItemAdapter(Activity context) {
            mContext = context;
            mArticles = new ArrayList<>();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_article, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            final Article article = mArticles.get(position);

            if (article != null) {
                Picasso.with(mContext)
                        .load(article.getThumbUrl())
                        .fit()
                        .centerCrop()
                        .into(viewHolder.image, new Callback() {
                            @Override
                            public void onSuccess() {
                                viewHolder.progress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                viewHolder.progress.setVisibility(View.GONE);
                            }
                        });
                viewHolder.title.setText(article.getTitle());
                viewHolder.author.setText(article.getAuthor());
                viewHolder.date.setText(UIUtils.getDateString(mContext, article.getPublishedDate(), Constants.FULL_DATE_FORMAT));

                AnimationUtils.animate(viewHolder, mPreviousPosition < position);
                mPreviousPosition = position;

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ArticleDetailActivity.class);
                        intent.putExtra(Constants.EXTRA_ARTICLE_ID, article.getId());
                        intent.putExtra(Constants.EXTRA_ARTICLE_POSITION, position);
                        if (UIUtils.isLollipop()) {
                            Pair<View, String> image = Pair.create((View)viewHolder.image, viewHolder.image.getTransitionName());
//                            Pair<View, String> title = Pair.create((View)viewHolder.title, viewHolder.title.getTransitionName());

                            mContext.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    mContext, image).toBundle());
                        } else {
                            mContext.startActivity(intent);
                        }

                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mArticles.size();
        }

        public Article getItem(int position) {
            return mArticles.get(position);
        }

        public void addAll(List<Article> articles) {
            mArticles.clear();

//            for (int i = 0; i < articles.size(); i++) {
//                mArticles.add(articles.get(i));
//                notifyItemInserted(i);
//            }
            mArticles.addAll(articles);
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(final OnItemClickListener itemClickListener) {
            mOnItemClickListener = itemClickListener;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private ImageView image;
            private TextView title;
            private TextView author;
            private TextView date;
            private ProgressBar progress;

            public ViewHolder(View base) {
                super(base);

                image = (ImageView) base.findViewById(R.id.image);
                progress = (ProgressBar) base.findViewById(R.id.progress);
                title = (TextView) base.findViewById(R.id.title);
                author = (TextView) base.findViewById(R.id.author);
                date = (TextView) base.findViewById(R.id.date);

                base.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, getAdapterPosition());
                }
            }
        }

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }
    }
}
