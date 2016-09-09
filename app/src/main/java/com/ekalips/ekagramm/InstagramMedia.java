package com.ekalips.ekagramm;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ekalips on 9/7/16.
 */

class InstagramMedia implements Serializable,Parcelable {
    private String type,description,mediaID;
    private List<String> tags = new ArrayList<>();
    private List<InstaComment> comments = new ArrayList<>();
    private int likesCount = 0; int commentsCount = 0;
    private String lowResolution,thumbnail,standardResolution;
    private int maxWidth,maxHeight, aspect;
    private String username,profilePic;
    private OnCommentsLoadedCallback callback;

    protected InstagramMedia(Parcel in) {
        type = in.readString();
        description = in.readString();
        mediaID = in.readString();
        tags = in.createStringArrayList();
        likesCount = in.readInt();
        commentsCount = in.readInt();
        lowResolution = in.readString();
        thumbnail = in.readString();
        standardResolution = in.readString();
        maxWidth = in.readInt();
        maxHeight = in.readInt();
        aspect = in.readInt();
        username = in.readString();
        profilePic = in.readString();
        in.readTypedList(comments,null);
    }

    public static final Creator<InstagramMedia> CREATOR = new Creator<InstagramMedia>() {
        @Override
        public InstagramMedia createFromParcel(Parcel in) {
            return new InstagramMedia(in);
        }

        @Override
        public InstagramMedia[] newArray(int size) {
            return new InstagramMedia[size];
        }
    };

    public OnCommentsLoadedCallback getCallback() {
        return callback;
    }

    public void setCallback(OnCommentsLoadedCallback callback) {
        this.callback = callback;
    }


    InstagramMedia(JSONObject object) throws JSONException {
        description = object.getJSONObject("caption").getString("text");
        likesCount = object.getJSONObject("likes").getInt("count");
        commentsCount = object.getJSONObject("comments").getInt("count");
        mediaID = object.getString("id");
        username = object.getJSONObject("user").getString("username");
        profilePic = object.getJSONObject("user").getString("profile_picture");
        for (int i = 0; i < object.getJSONArray("tags").length(); i++) {
            tags.add(String.valueOf(object.getJSONArray("tags").get(i)));
        }
        if ((type = object.getString("type")).equals("image"))
        {
            lowResolution = object.getJSONObject("images").getJSONObject("low_resolution").getString("url");
            thumbnail = object.getJSONObject("images").getJSONObject("thumbnail").getString("url");
            standardResolution = object.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
            maxWidth = object.getJSONObject("images").getJSONObject("standard_resolution").getInt("width");
            maxHeight = object.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
            aspect = maxHeight/maxWidth;
        }
        else throw new UnsupportedOperationException();
        Log.d("CONVERT","HANDLED");
    }

    public void initiateComments(JSONArray object) throws JSONException {
        for (int i = 0; i < object.length(); i++) {
            comments.add(new InstaComment(object.getJSONObject(i)));
        }
        callback.onMethodCallback();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMediaID() {
        return mediaID;
    }

    public void setMediaID(String mediaID) {
        this.mediaID = mediaID;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<InstaComment> getComments() {
        return comments;
    }

    public void setComments(List<InstaComment> comments) {
        this.comments = comments;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getLowResolution() {
        return lowResolution;
    }

    public void setLowResolution(String lowResolution) {
        this.lowResolution = lowResolution;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getStandardResolution() {
        return standardResolution;
    }

    public void setStandardResolution(String standardResolution) {
        this.standardResolution = standardResolution;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getAspect() {
        return aspect;
    }

    public void setAspect(int aspect) {
        this.aspect = aspect;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getType());
        parcel.writeString(getDescription());
        parcel.writeString(getMediaID());
        parcel.writeStringList(getTags());
        parcel.writeInt(getLikesCount());
        parcel.writeInt(getCommentsCount());
        parcel.writeString(getLowResolution());
        parcel.writeString(getThumbnail());
        parcel.writeString(getStandardResolution());
        parcel.writeInt(getMaxWidth());
        parcel.writeInt(getMaxHeight());
        parcel.writeInt(getAspect());
        parcel.writeString(getUsername());
        parcel.writeString(getProfilePic());
        parcel.writeTypedList(comments);
    }
}
