package com.example.xyzreader.object;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Article object
 *
 * Created by kyleparker on 11/5/2015.
 */
public class Article implements Parcelable, Comparable<Article> {
    private double aspectRatio;
    private String author;
    private String body;
    private int id;
    private String photoUrl;
    private long publishedDate;
    private String serverId;
    private String title;
    private String thumbUrl;

    public Article() { }

    private Article(Parcel in) {
        aspectRatio = in.readDouble();
        author = in.readString();
        body = in.readString();
        id = in.readInt();
        photoUrl = in.readString();
        publishedDate = in.readLong();
        serverId = in.readString();
        title = in.readString();
        thumbUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(aspectRatio);
        dest.writeString(author);
        dest.writeString(body);
        dest.writeInt(id);
        dest.writeString(photoUrl);
        dest.writeLong(publishedDate);
        dest.writeString(serverId);
        dest.writeString(title);
        dest.writeString(thumbUrl);
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public double getAspectRatio() {
        return aspectRatio;
    }
    public void setAspectRatio(double value)  {
        this.aspectRatio = value;
    }

    public String getAuthor() {
        return author;
    }
    public void setAuthor(String value)  {
        this.author = value;
    }

    public String getBody() {
        return body;
    }
    public void setBody(String value)  {
        this.body = value;
    }

    public int getId() {
        return id;
    }
    public void setId(int value)  {
        this.id = value;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    public void setPhotoUrl(String value)  {
        this.photoUrl = value;
    }

    public long getPublishedDate() {
        return publishedDate;
    }
    public void setPublishedDate(long value)  {
        this.publishedDate = value;
    }

    public String getServerId() {
        return serverId;
    }
    public void setServerId(String value)  {
        this.serverId = value;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String value)  {
        this.title = value;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }
    public void setThumbUrl(String value)  {
        this.thumbUrl = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result + Double.toString(aspectRatio).hashCode();
        result = prime * result + ((author == null) ? 0 : author.hashCode());
        result = prime * result + ((body == null) ? 0 : body.hashCode());
        result = prime * result + Integer.toString(id).hashCode();
        result = prime * result + ((photoUrl == null) ? 0 : photoUrl.hashCode());
        result = prime * result + Long.toString(publishedDate).hashCode();
        result = prime * result + ((serverId == null) ? 0 : serverId.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((thumbUrl == null) ? 0 : thumbUrl.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }

        if (obj == null) { return false; }

        if (getClass() != obj.getClass()) { return false; }

        Article other = (Article) obj;

        if (aspectRatio != other.aspectRatio) { return false; }

        if (author == null) {
            if (other.author != null) { return false; }
        } else if (!author.equals(other.author)) { return false; }

        if (body == null) {
            if (other.body != null) { return false; }
        } else if (!body.equals(other.body)) { return false; }

        if (id != other.id) { return false; }

        if (photoUrl == null) {
            if (other.photoUrl != null) { return false; }
        } else if (!photoUrl.equals(other.photoUrl)) { return false; }

        if (publishedDate != other.publishedDate) { return false; }

        if (serverId == null) {
            if (other.serverId != null) { return false; }
        } else if (!serverId.equals(other.serverId)) { return false; }

        if (title == null) {
            if (other.title != null) { return false; }
        } else if (!title.equals(other.title)) { return false; }

        if (thumbUrl == null) {
            if (other.thumbUrl != null) { return false; }
        } else if (!thumbUrl.equals(other.thumbUrl)) { return false; }

        return true;
    }

    public int compareTo(@NonNull Article another) {
        // sort descending, most recent first
        String secondId = Integer.toString(another.id);
        String firstId = Integer.toString(id);

        return secondId.compareTo(firstId);
    }
}
