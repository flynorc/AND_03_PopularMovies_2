package com.flynorc.popularmovies_stage2;

/**
 * Created by Flynorc on 05-Mar-18.
 */

class MovieVideo {

    private String youtubeKey;
    private String type;
    private String name;

    public MovieVideo( String youtubeKey, String type, String name) {
        this.youtubeKey = youtubeKey;
        this.type = type;
        this.name = name;
    }

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
}
