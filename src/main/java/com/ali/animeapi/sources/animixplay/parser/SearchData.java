package com.ali.animeapi.sources.animixplay.parser;

public class SearchData {

    private String title;
    private String url;
    private String cover;

    public SearchData(String title, String url, String cover) {
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
