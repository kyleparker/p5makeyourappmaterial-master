package com.example.xyzreader.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.example.xyzreader.object.Article;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for the content provider
 *
 * Created by kyleparker on 11/5/2015.
 */
public class AppProviderUtils {
    private final ContentResolver mContentResolver;

    public AppProviderUtils(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }

    public int bulkInsertArticle(ContentValues[] contentValues) {
        return mContentResolver.bulkInsert(ArticleColumns.CONTENT_URI, contentValues);
    }

    public Article getArticle(int articleId) {
        Uri uri = Uri.parse(ArticleColumns.CONTENT_URI + "/" + articleId);

        Cursor cursor = mContentResolver.query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return createArticle(cursor);
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public List<Article> getArticleList() {
        ArrayList<Article> list = new ArrayList<>();

        Cursor cursor = mContentResolver.query(ArticleColumns.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            list.ensureCapacity(cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    list.add(createArticle(cursor));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return list;
    }

    /**
     *
     * @param cursor
     * @return
     */
    private Article createArticle(Cursor cursor) {
        int idxId = cursor.getColumnIndex(ArticleColumns._ID);
        int idxAspectRatio = cursor.getColumnIndex(ArticleColumns.ASPECT_RATIO);
        int idxAuthor = cursor.getColumnIndex(ArticleColumns.AUTHOR);
        int idxBody = cursor.getColumnIndex(ArticleColumns.BODY);
        int idxPhotoUrl = cursor.getColumnIndex(ArticleColumns.PHOTO_URL);
        int idxPublishedDate = cursor.getColumnIndex(ArticleColumns.PUBLISHED_DATE);
        int idxServerId = cursor.getColumnIndex(ArticleColumns.SERVER_ID);
        int idxThumbUrl = cursor.getColumnIndex(ArticleColumns.THUMB_URL);
        int idxTitle = cursor.getColumnIndex(ArticleColumns.TITLE);

        Article article = new Article();

        if (idxId > -1) {
            article.setId(cursor.getInt(idxId));
        }
        if (idxAspectRatio > -1) {
            article.setAspectRatio(cursor.getDouble(idxAspectRatio));
        }
        if (idxAuthor > -1) {
            article.setAuthor(cursor.getString(idxAuthor));
        }
        if (idxBody > -1) {
            article.setBody(cursor.getString(idxBody));
        }
        if (idxPhotoUrl > -1) {
            article.setPhotoUrl(cursor.getString(idxPhotoUrl));
        }
        if (idxPublishedDate > -1) {
            article.setPublishedDate(cursor.getLong(idxPublishedDate));
        }
        if (idxServerId > -1) {
            article.setServerId(cursor.getString(idxServerId));
        }
        if (idxThumbUrl > -1) {
            article.setThumbUrl(cursor.getString(idxThumbUrl));
        }
        if (idxTitle > -1) {
            article.setTitle(cursor.getString(idxTitle));
        }

        return article;
    }

    public ContentValues createContentValues(Article obj) {
        ContentValues contentValues = new ContentValues();
        
        contentValues.put(ArticleColumns.ASPECT_RATIO, obj.getAspectRatio());
        contentValues.put(ArticleColumns.AUTHOR, obj.getAuthor());
        contentValues.put(ArticleColumns.BODY, obj.getBody());
        contentValues.put(ArticleColumns.PHOTO_URL, obj.getPhotoUrl());
        contentValues.put(ArticleColumns.PUBLISHED_DATE, obj.getPublishedDate());
        contentValues.put(ArticleColumns.SERVER_ID, obj.getServerId());
        contentValues.put(ArticleColumns.THUMB_URL, obj.getThumbUrl());
        contentValues.put(ArticleColumns.TITLE, obj.getTitle());

        return contentValues;
    }

    /**
     * A factory which can produce instances of {@link AppProviderUtils}
     */
    public static class Factory {
        private static Factory instance = new Factory();

        /**
         * Creates and returns an instance of {@link AppProviderUtils} which uses the given context to access its data.
         */
        public static AppProviderUtils get(Context context) {
            return instance.newForContext(context);
        }

        /**
         * Creates an instance of {@link AppProviderUtils}.
         */
        protected AppProviderUtils newForContext(Context context) {
            return new AppProviderUtils(context.getContentResolver());
        }
    }
}
