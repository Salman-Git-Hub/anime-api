package com.ali.animeapi.models;

public class Search {

    private String title;
    private String url;
    private String cover;

    public Search (String title, String url, String cover) {
        this.title = title;
        this.url = url;
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getCover() {
        return cover;
    }

}
