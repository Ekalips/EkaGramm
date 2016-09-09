package com.ekalips.ekagramm;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by ekalips on 9/8/16.
 */

class InstaComment implements Serializable,Parcelable
{
    private long createdTime;
    private String text;
    private String authorUsername;
    private String profilePictureUrl;

    public InstaComment(JSONObject object) throws JSONException {
        createdTime = object.getLong("created_time");
        text = object.getString("text");
        authorUsername = object.getJSONObject("from").getString("username");
        profilePictureUrl = object.getJSONObject("from").getString("profile_picture");
    }

    protected InstaComment(Parcel in) {
        createdTime = in.readLong();
        text = in.readString();
        authorUsername = in.readString();
        profilePictureUrl = in.readString();
    }

    public static final Creator<InstaComment> CREATOR = new Creator<InstaComment>() {
        @Override
        public InstaComment createFromParcel(Parcel in) {
            return new InstaComment(in);
        }

        @Override
        public InstaComment[] newArray(int size) {
            return new InstaComment[size];
        }
    };

    public long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeLong(createdTime);
        parcel.writeString(text);
        parcel.writeString(authorUsername);
        parcel.writeString(profilePictureUrl);
    }
}
