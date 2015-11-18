package com.example.xyzreader.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

/**
 * Content provider
 *
 * Created by kyleparker on 11/5/2015.
 */
public class AppContentProvider extends ContentProvider {
    private static final String TAG = AppContentProvider.class.getSimpleName();

    public static final String AUTHORITY = "com.example.xyzreader";
    public static final String CONTENT_URI = "content://com.example.xyzreader/";

    private static final String DATABASE_NAME = "xyzreader.db";
    private static final int DATABASE_VERSION = 1;

    private static Context mContext;
    private static SQLiteDatabase mDb;
    private final UriMatcher mUriMatcher;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(ArticleColumns.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + ArticleColumns.TABLE_NAME);
            onCreate(db);
        }
    }

    private enum UrlType {
        ARTICLE, ARTICLE_ID
    }

    public AppContentProvider() {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(AUTHORITY, ArticleColumns.TABLE_NAME, UrlType.ARTICLE.ordinal());
        mUriMatcher.addURI(AUTHORITY, ArticleColumns.TABLE_NAME + "/#", UrlType.ARTICLE_ID.ordinal());
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(getContext());

        try {
            mDb = databaseHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to open database for writing", e);
        }
        return mDb != null;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String sortOrder = null;

        switch (getUrlType(uri)) {
            case ARTICLE:
                queryBuilder.setTables(ArticleColumns.TABLE_NAME);
                sortOrder = sort != null ? sort : ArticleColumns.DEFAULT_SORT_ORDER;
                break;
            case ARTICLE_ID:
                queryBuilder.setTables(ArticleColumns.TABLE_NAME);
                queryBuilder.appendWhere(ArticleColumns._ID + " = " + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri " + uri);
        }

        Cursor cursor = queryBuilder.query(mDb, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(mContext.getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (getUrlType(uri)) {
            case ARTICLE:
                return ArticleColumns.CONTENT_TYPE;
            case ARTICLE_ID:
                return ArticleColumns.CONTENT_ITEMTYPE;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        if (contentValues == null) {
            contentValues = new ContentValues();
        }
        Uri result = null;
        try {
            mDb.beginTransaction();
            result = insertContentValues(uri, getUrlType(uri), contentValues, false);
            mDb.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e("error", ex.getMessage());
            ex.printStackTrace();
        } finally {
            mDb.endTransaction();
        }
        return result;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        int numInserted = 0;
        try {
            // Use a transaction in order to make the insertions run as a single batch
            mDb.beginTransaction();

            UrlType urlType = getUrlType(uri);
            for (numInserted = 0; numInserted < values.length; numInserted++) {
                ContentValues contentValues = values[numInserted];
                if (contentValues == null) {
                    contentValues = new ContentValues();
                }
                insertContentValues(uri, urlType, contentValues, true);
            }
            mDb.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e("error", ex.getMessage());
            ex.printStackTrace();
        } finally {
            mDb.endTransaction();
        }
        return numInserted;
    }

    @Override
    public int delete(@NonNull Uri uri, String where, String[] selectionArgs) {
        String table;

        switch (getUrlType(uri)) {
            case ARTICLE:
                table = ArticleColumns.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri " + uri);
        }

        Log.w(TAG, "Deleting table " + table);
        int count = 0;
        try {
            mDb.beginTransaction();
            count = mDb.delete(table, where, selectionArgs);
            mDb.setTransactionSuccessful();
        } catch (Exception ex) {
            Log.e("error", ex.getMessage());
            ex.printStackTrace();
        } finally {
            mDb.endTransaction();
        }

        mContext.getContentResolver().notifyChange(uri, null, true);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String where, String[] selectionArgs) {
        String table;
        String whereClause;
        switch (getUrlType(uri)) {
            case ARTICLE:
                table = ArticleColumns.TABLE_NAME;
                whereClause = where;
                break;
            case ARTICLE_ID:
                table = ArticleColumns.TABLE_NAME;
                whereClause = ArticleColumns._ID + "=" + uri.getPathSegments().get(1);
                if (!TextUtils.isEmpty(where)) {
                    whereClause += " AND (" + where + ")";
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri " + uri);
        }

        int count = 0;
        try {
            mDb.beginTransaction();
            count = mDb.update(table, values, whereClause, selectionArgs);
            mDb.setTransactionSuccessful();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mDb.endTransaction();
        }
        mContext.getContentResolver().notifyChange(uri, null, true);
        return count;
    }

    /**
     * Gets the {@link com.example.xyzreader.data.AppContentProvider.UrlType} for a url.
     *
     * @param uri
     */
    private UrlType getUrlType(Uri uri) {
        return UrlType.values()[mUriMatcher.match(uri)];
    }

    /**
     * Inserts a content based on the url type.
     *
     * @param uri the content uri
     * @param urlType the url type
     * @param contentValues the content values
     */
    private Uri insertContentValues(Uri uri, UrlType urlType, ContentValues contentValues, boolean bulkInsert) {
        switch (urlType) {
            case ARTICLE:
                return insertArticles(uri, contentValues, bulkInsert);
            default:
                throw new IllegalArgumentException("Unknown url " + uri);
        }
    }

    /**
     * Inserts the item
     *
     * @param uri the content uri
     * @param contentValues the content values
     */
    private Uri insertArticles(Uri uri, ContentValues contentValues, boolean bulkInsert) {
        if (TextUtils.isEmpty(contentValues.getAsString(ArticleColumns.SERVER_ID))) {
            throw new SQLiteException("Failed to insert row into " + uri);
        }

        long rowId = bulkInsert ?
                mDb.insertWithOnConflict(ArticleColumns.TABLE_NAME, ArticleColumns.SERVER_ID, contentValues, SQLiteDatabase.CONFLICT_REPLACE) :
                mDb.insert(ArticleColumns.TABLE_NAME, ArticleColumns.SERVER_ID, contentValues);

        if (rowId >= 0) {
            uri = ContentUris.appendId(ArticleColumns.CONTENT_URI.buildUpon(), rowId).build();
            mContext.getContentResolver().notifyChange(uri, null, true);
            return uri;
        }

        throw new SQLiteException("Failed to insert row into " + uri);
    }
}
