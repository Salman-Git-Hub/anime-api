package com.ali.animeapi.models;


public class Recent {
    private String title;
    private String episode;
    private String url;
    private String cover;
    private String date;
    private String last;

    public Recent(String title, String episode, String url, String cover, String date) {
        this.title = title;
        this.episode = episode;
        this.url = url;
        this.cover = cover;
        this.date = date;
    }

    public Recent(String last) {
        this.last = last;
    }

    public String getTitle() {
        return title;
    }

    public String getEpisode() {
        return episode;
    }

    public String getUrl() {
        return url;
    }

    public String getCover() {
        return cover;
    }

    public String getDate() {
        return date;
    }

    public String getLast() {
        return last;
    }


}
