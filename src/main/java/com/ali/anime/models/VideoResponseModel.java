package com.ali.anime.models;

public class VideoResponseModel {
    private final String streamUrl;
    private final String siteUrl;
    private final String title;
    private final String episode;


    public VideoResponseModel(String streamUrl, String siteUrl, String title, String episode) {
        this.streamUrl = streamUrl;
        this.siteUrl = siteUrl;
        this.title = title;
        this.episode = episode;
    }

    public String getStreamUrl() {
        return streamUrl;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getEpisode() {
        return episode;
    }
}
