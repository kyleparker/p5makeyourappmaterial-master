package com.example.xyzreader.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kyleparker on 11/5/2015.
 */
public interface ArticleColumns extends BaseColumns {

    String TABLE_NAME = "article";
    Uri CONTENT_URI = Uri.parse(AppContentProvider.CONTENT_URI + TABLE_NAME);
    String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.bsu" + TABLE_NAME;
    String CONTENT_ITEMTYPE = "vnd.android.cursor.item/vnd.bsu" + TABLE_NAME;

    String _ID = "_id";
    String SERVER_ID = "server_id";
    String TITLE = "title";
    String AUTHOR = "author";
    String BODY = "body";
    String THUMB_URL = "thumb_url";
    String PHOTO_URL = "photo_url";
    String ASPECT_RATIO = "aspect_ratio";
    String PUBLISHED_DATE = "published_date";

    String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + SERVER_ID + " TEXT, "
                    + TITLE + " TEXT NOT NULL, "
                    + AUTHOR + " TEXT NOT NULL, "
                    + BODY + " TEXT NOT NULL, "
                    + THUMB_URL + " TEXT NOT NULL, "
                    + PHOTO_URL + " TEXT NOT NULL, "
                    + ASPECT_RATIO + " REAL NOT NULL DEFAULT 1.5, "
                    + PUBLISHED_DATE + " INTEGER NOT NULL DEFAULT 0, "
                    + " UNIQUE (" + SERVER_ID + ") ON CONFLICT REPLACE"
                    + ");";

    String DEFAULT_SORT_ORDER = TABLE_NAME + "." + PUBLISHED_DATE + " DESC";
}

