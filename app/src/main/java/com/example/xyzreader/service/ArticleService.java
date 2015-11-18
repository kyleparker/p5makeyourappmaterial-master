package com.example.xyzreader.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.xyzreader.data.AppProviderUtils;
import com.example.xyzreader.object.Article;
import com.example.xyzreader.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Service to retrieve the articles from the backend source
 *
 * Created by kyleparker on 11/6/2015.
 */
public class ArticleService extends IntentService {
    private static final String TAG = ArticleService.class.getSimpleName();

    private static Context mContext;
    private static AppProviderUtils mProvider;

    public ArticleService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mContext = this;
        mProvider = AppProviderUtils.Factory.get(mContext);

        JSONArray jsonData = getData();
        processData(jsonData);
        handleBroadcast();
    }

    private JSONArray getData() {
        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        String jsonData = null;

        try {
            Uri uriFetch = Uri.parse("https://dl.dropboxusercontent.com/u/231329/xyzreader_data/data.json");

            URL fetch = new URL(uriFetch.toString());
            httpURLConnection = (HttpURLConnection) fetch.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line);
                buffer.append("\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            jsonData = buffer.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error fetching items JSON", e);
            return null;
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error Closing Stream");
                }
            }
        }

        // Parse JSON
        try {
            JSONTokener tokener = new JSONTokener(jsonData);
            Object val = tokener.nextValue();
            if (!(val instanceof JSONArray)) {
                throw new JSONException("Expected JSONArray");
            }
            return (JSONArray) val;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing items JSON", e);
        }

        return null;
    }

    /**
     * Send the broadcast intent to notify the calling activity that the service completed
     */
    private void handleBroadcast() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.RECEIVER_ARTICLE_COMPLETED);
        mContext.sendBroadcast(broadcastIntent);
    }

    private void processData(JSONArray articles) {
        Locale locale = mContext.getResources().getConfiguration().locale;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale);
        // explicitly set timezone of input if needed
        df.setTimeZone(TimeZone.getTimeZone("Zulu"));

        try {
            if (articles == null) {
                throw new JSONException("Invalid parsed item array" );
            }

            int j = 0;
            ContentValues[] contentValues = new ContentValues[articles.length()];

            for (int i = 0; i < articles.length(); i++) {
                JSONObject object = articles.getJSONObject(i);

                double aspectRatio;
                long publishedDate;

                try {
                    aspectRatio = Double.valueOf(object.getString("aspect_ratio"));
                } catch (Exception ex) {
                    aspectRatio = 0;
                }

                try {
                    Date date = df.parse(object.getString("published_date"));
                    publishedDate = date.getTime();
                } catch (Exception ex) {
                    publishedDate = -1L;
                }

                Article article = new Article();
                article.setAspectRatio(aspectRatio);
                article.setAuthor(object.getString("author"));
                article.setBody(object.getString("body"));
                article.setPhotoUrl(object.getString("photo"));
                article.setPublishedDate(publishedDate);
                article.setServerId(object.getString("id"));
                article.setTitle(object.getString("title"));
                article.setThumbUrl(object.getString("thumb"));

                contentValues[j] = mProvider.createContentValues(article);
                j++;
            }

            mProvider.bulkInsertArticle(contentValues);
        } catch (JSONException e) {
            Log.e(TAG, "Error updating content.", e);
        }

    }
}
