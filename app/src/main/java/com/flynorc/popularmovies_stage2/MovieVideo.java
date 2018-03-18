package com.flynorc.popularmovies_stage2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Flynorc on 05-Mar-18.
 */

class MovieVideo implements Parcelable{

    private String youtubeKey;
    private String type;
    private String name;

    public MovieVideo( String youtubeKey, String type, String name) {
        this.youtubeKey = youtubeKey;
        this.type = type;
        this.name = name;
    }

    protected MovieVideo(Parcel in) {
        youtubeKey = in.readString();
        type = in.readString();
        name = in.readString();
    }

    public static final Creator<MovieVideo> CREATOR = new Creator<MovieVideo>() {
        @Override
        public MovieVideo createFromParcel(Parcel in) {
            return new MovieVideo(in);
        }

        @Override
        public MovieVideo[] newArray(int size) {
            return new MovieVideo[size];
        }
    };

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getYoutubeKey() {
        return youtubeKey;
    }

    public String getYoutubeUrl() {
        return "https://www.youtube.com/watch?v=" + youtubeKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(youtubeKey);
        parcel.writeString(type);
        parcel.writeString(name);
    }
}
